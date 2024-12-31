package com.mhealth.admin.service;

import com.mhealth.admin.config.AuthConfig;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.config.Utility;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.LoginResponseDto;
import com.mhealth.admin.dto.dto.PermissionDto;
import com.mhealth.admin.dto.dto.VerifyLoginOtp;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.LoginRequest;
import com.mhealth.admin.dto.response.PermissionRoleDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.AuthKey;
import com.mhealth.admin.model.Permission;
import com.mhealth.admin.model.PermissionRole;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.AuthKeyRepository;
import com.mhealth.admin.repository.PermissionRepository;
import com.mhealth.admin.repository.PermissionRoleRepository;
import com.mhealth.admin.model.UserOTP;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.AuthKeyRepository;
import com.mhealth.admin.repository.UserOTPRepository;
import com.mhealth.admin.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserOTPRepository userOTPRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthKeyRepository authKeyRepository;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Utility utility;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionRoleRepository permissionRoleRepository;

    @Autowired
    private PublicService publicService;

    public ResponseEntity<Response> login(LoginRequest request, Locale locale) {
        Response responseDto = new Response();
        String message;
        String statusCode = Constants.SUCCESS_CODE;
        Status status = Status.FAILED;
        LoginResponseDto data = null; // Using DTO instead of Map

        try {
            // Fetch user by contact number and type
            Users existingUser = usersRepository.findByContactNumber(request.getContactNumber()).orElse(null);

            String countryCode = request.getCountryCode().replace("+", "");
            if (existingUser == null || !existingUser.getCountryCode().equalsIgnoreCase(countryCode)) {
                // User not found or country code mismatch
                message = messageSource.getMessage(Constants.USER_NOT_FOUND, null, locale);
                responseDto.setStatus(Status.FAILED);
                responseDto.setMessage(message);
                responseDto.setCode(Constants.BLANK_DATA_GIVEN_CODE);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto);
            }

            if (existingUser.getType().equals(UserType.Patient)) {
                message = messageSource.getMessage(Constants.USER_NOT_FOUND, null, locale);
                responseDto.setStatus(Status.FAILED);
                responseDto.setMessage(message);
                responseDto.setCode(Constants.BLANK_DATA_GIVEN_CODE);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto);
            }

            if (!utility.md5Hash(request.getPassword()).equals(existingUser.getPassword())) {
                // Password mismatch
                message = messageSource.getMessage(Constants.INVALID_PASSWORD, null, locale);
                responseDto.setStatus(Status.FAILED);
                responseDto.setMessage(message);
                responseDto.setCode(Constants.DATA_NOT_FOUND_CODE); // Use a specific code for invalid password
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDto); // 401 Unauthorized
            }

            // Successful login
            String token = authConfig.generateToken(request.getContactNumber(), existingUser.getUserId());
            boolean isInternational = existingUser.getIsInternational().equals(YesNo.Yes);

            PermissionRoleDto permission = null;
            try {
                PermissionRole role = permissionRoleRepository.findByUserType(existingUser.getType()).orElse(null);
                if(role!=null){
                    permission = getPermissions(role);
                }
            }catch (Exception e){

            }

            // Populate the DTO
            data = new LoginResponseDto(
                    existingUser.getUserId().toString(),
                    token,
                    isInternational,
                    permission
            );

            message = messageSource.getMessage(Constants.USER_LOGIN_IS_SUCCESS, null, locale);
            statusCode = Constants.SUCCESS_CODE;
            status = Status.SUCCESS;

            saveNewSession(existingUser.getUserId(), token, UUID.randomUUID().toString(), UserType.Superadmin);

            responseDto.setStatus(status);
            responseDto.setMessage(message);
            responseDto.setCode(statusCode);
            responseDto.setData(data);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error found in super-admin login : {}", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.SOMETHING_WENT_WRONG, null, locale)
            ));
        }
    }

    public ResponseEntity<?> generateOtpForForgotPassword(String contactNumber, Locale locale) {

        if (contactNumber == null || contactNumber.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.BLANK_DATA_GIVEN, null, locale)
            ));
        }
        Optional<Users> user = usersRepository.findByContactNumberAndType(contactNumber, UserType.Superadmin);
        if (user.isPresent()) {
            int otp = publicService.saveNewOtp(user.get());

            Map<String, Object> temp = new HashMap<>();
            temp.put("user", user.get());
            temp.put("otp", String.valueOf(otp));
            String message = publicService.sendMessage(temp, Constants.OTP_TO_RESET_PASSWORD, locale);

            return ResponseEntity.status(HttpStatus.OK).body(new Response(
                    Status.SUCCESS,
                    Constants.SUCCESS_CODE,
                    messageSource.getMessage(Constants.OTP_SEND_SUCCESSFULLY, null, locale)
            ));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.CONTACT_NUMBER_NOT_EXIST, null, locale)
        ));
    }

    public ResponseEntity<?> verifyOtpForForgotPassword(VerifyLoginOtp request, Locale locale) {
        if(request.getOtp() == null || request.getOtp().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().isEmpty()
                || request.getContactNumber() == null || request.getContactNumber().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.BLANK_DATA_GIVEN, null, locale)
            ));
        }
        Optional<Users> user = usersRepository.findByContactNumberAndType(request.getContactNumber(), UserType.Superadmin);
        if (user.isPresent()) {
            UserOTP userOTP = userOTPRepository.findFirstByUserIdAndIsFromOrderByIdDesc(user.get().getUserId(), "Forgotpassword");
            if(userOTP != null){
                return publicService.processOtp(user.get(), userOTP, request, locale);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new Response(
                    Status.FAILED,
                    Constants.DATA_NOT_FOUND_CODE,
                    messageSource.getMessage(Constants.NO_RECORD_FOUND, null, locale)
            ));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                Status.FAILED,
                Constants.DATA_NOT_FOUND_CODE,
                messageSource.getMessage(Constants.CONTACT_NUMBER_NOT_EXIST, null, locale)
        ));
    }

    public boolean isSessionValid(String username, String authKey) {
        Users user = usersRepository.findByContactNumber(username).orElse(null);
        if(user!=null){
            Optional<AuthKey> session = authKeyRepository.findByUserIdAndLoginType(user.getUserId(), UserType.Patient);
            return session.isPresent() && session.get().getAuthKey().equals(authKey);
        }
        return false;
    }

    public AuthKey saveNewSession(Integer userId, String authKey, String deviceToken, UserType loginType) {
        // Invalidate any existing session for the user and login type
        invalidateOldSessions(userId, loginType);

        // Create and save the new session
        AuthKey newSession = new AuthKey();
        newSession.setUserId(userId);
        newSession.setAuthKey(authKey);
        newSession.setDeviceToken(deviceToken); // Can be null for web sessions
        newSession.setLoginType(loginType);
        newSession.setCreatedDate(new Date());

        return authKeyRepository.save(newSession);
    }

    public boolean invalidateOldSessions(Integer userId, UserType loginType) {
        List<AuthKey> existingSessions = authKeyRepository.findAllByUserIdAndLoginType(userId, loginType);

        if (!existingSessions.isEmpty()) {
            authKeyRepository.deleteAll(existingSessions);
            return true;
        }

        return false;
    }

    public PermissionRoleDto getPermissions(PermissionRole role){
        List<String> rolesStringIds = Arrays.asList(role.getPermissions().split(","));
        List<Integer> ids = new ArrayList<>();
        for(String s:rolesStringIds){
            ids.add(Integer.parseInt(s));
        }
        List<Permission> permissionList = new ArrayList<>();
        if(!ids.isEmpty()){
            permissionList = permissionRepository.findByIds(ids);
        }

        List<Permission> level0 = new ArrayList<>();
        List<Permission> level1 = new ArrayList<>();
        List<Permission> level2 = new ArrayList<>();

        for(Permission p:permissionList){
            if(p.getLevel()==0){
                level0.add(p);
            }else if(p.getLevel()==1){
                level1.add(p);
            } else if (p.getLevel()==2) {
                level2.add(p);
            }
        }

        List<PermissionDto> data = getTreeStructure(level0,level1,level2);
        return new PermissionRoleDto(data,role);
    }

    public List<PermissionDto> getTreeStructure(
            List<Permission> firstLevel,List<Permission>second,List<Permission>third){
        List<PermissionDto> response = new ArrayList<>();
        for(Permission p:firstLevel){
            response.add(new PermissionDto(p));
        }
        for(PermissionDto dto :response){
            for(Permission p:second){
                if(p.getParent()!= null && p.getParent().getId()==dto.getId()){
                    dto.getSubPermission().add(new PermissionDto(p));
                }
            }
        }
        for(PermissionDto external :response){
            for(PermissionDto internal :external.getSubPermission()){
                for(Permission p:third){
                    if(p.getParent()!=null && p.getParent().getId()==internal.getId()){
                        internal.getSubPermission().add(new PermissionDto(p));
                    }
                }
            }
        }

        return response;
    }
}