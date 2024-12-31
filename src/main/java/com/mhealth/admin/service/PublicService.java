package com.mhealth.admin.service;

import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.sms.SMSAggregator;
import com.mhealth.admin.sms.SMSApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.config.Utility;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.VerifyLoginOtp;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Notification;
import com.mhealth.admin.model.UserOTP;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.UserOTPRepository;
import com.mhealth.admin.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class PublicService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserOTPRepository userOTPRepository;
    @Autowired
    private Utility utility;
    @Value("${app.fixed.otp}")
    private boolean isFixedOtp;
    @Value("${app.zone.name}")
    private String zone;
    @Value("${app.otp.validity.time}")
    private Integer OTP_VALIDITY_TIME;
    @Value("${app.sms.sent}")
    private boolean smsSent;
    @Autowired
    private MessageSource messageSource;

    @Value("${m-health.country}")
    private String mHealthCountry;

    @Autowired
    private SMSApiService smsApiService;

    public int saveNewOtp(Users user) {
        int otp = isFixedOtp ? 123456 : new Random().nextInt(900000) + 100000;
        UserOTP userOtp = new UserOTP();
        userOtp.setUserId(user.getUserId());
        userOtp.setOtp(utility.md5Hash((String.valueOf(otp))));
        userOtp.setExpiredAt(LocalDateTime.now(ZoneId.of(zone)).plusMinutes(OTP_VALIDITY_TIME));
        userOtp.setIsFrom("Forgotpassword");
        userOtp.setType("Superadmin");
        userOtp.setStatus("0");
        userOTPRepository.save(userOtp);
        log.info("Generated and saved OTP for user with mobile: {}", user.getContactNumber());
        return otp;
    }

    public String sendMessage(Map<String, Object> temp, String scenario, Locale locale) {
        Users users = (Users) temp.get("user");
        if(users.getNotificationLanguage().equalsIgnoreCase("en")){
            locale = Locale.ENGLISH;
        }else locale = new Locale("so");

        Notification notification = new Notification();

        String message = messageSource.getMessage(Constants.OTP_TO_RESET_PASSWORD, null, locale);;
        if(scenario.equalsIgnoreCase(Constants.OTP_TO_RESET_PASSWORD)){
            message = message.replace("{{otp}}", (String)temp.get("otp"));

            if(smsSent){
                if (users.getIsInternational().equals(YesNo.Yes))
                    smsApiService.sendMessage("+" + users.getCountryCode() + users.getContactNumber(), message, SMSAggregator.TWILIO);
                else
                    smsApiService.sendMessage("+" + users.getCountryCode() + users.getContactNumber(), message, mHealthCountry);
            }
        }
        return message;
    }

    public ResponseEntity<?> processOtp(Users user, UserOTP userOTP, VerifyLoginOtp request, Locale locale) {
        String message;
        Status status = Status.FAILED;
        if (!utility.md5Hash(request.getOtp()).equals(userOTP.getOtp())) {
            message = messageSource.getMessage(Constants.OTP_NOT_MATCHED, null, locale);
        } else {
            if (LocalDateTime.now(ZoneId.of(zone)).isAfter(userOTP.getExpiredAt())) {
                message = messageSource.getMessage(Constants.OTP_EXPIRES, null, locale);
            } else {
                log.info("Verified OTP with Contact Number: {}", user.getContactNumber());
                String encodedNewPassword = utility.md5Hash(request.getNewPassword());
                user.setPassword(encodedNewPassword);
                usersRepository.save(user);

                userOTP.setStatus("1");
                userOTPRepository.save(userOTP);

                message = messageSource.getMessage(Constants.Password_Reset, null, locale);
                status = Status.SUCCESS;
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(
                status,
                Constants.SUCCESS_CODE,
                message
        ));
    }

}
