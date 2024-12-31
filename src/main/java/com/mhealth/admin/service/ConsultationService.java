package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.consultationDto.*;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.City;
import com.mhealth.admin.model.Country;
import com.mhealth.admin.model.State;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.CityRepository;
import com.mhealth.admin.repository.CountryRepository;
import com.mhealth.admin.repository.StateRepository;
import com.mhealth.admin.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class ConsultationService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EntityManager entityManager;
    @Value("${app.country.code.for.patient}")
    private String countryCode;
    @Value("${app.zone.name}")
    private String zone;
    @Autowired
    private PublicService publicService;
    public ResponseEntity<Response> searchPatient(SearchPatientRequest request, Locale locale) {
        try {
            log.info("Entering into searchPatient api : {}", request);
            if (request.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                        Status.FAILED,
                        Constants.BLANK_DATA_GIVEN_CODE,
                        messageSource.getMessage(Constants.BLANK_DATA_GIVEN, null, locale),
                        new ArrayList<>()
                ));
            }
            StringBuilder sb = getStringBuilder(request);

            //generate Query
            int size = request.getSize()== null ? 20 : request.getSize();
            int page = request.getPage() == null ? 0 : request.getPage();
            Query query = entityManager.createQuery(sb.toString(), Users.class);
            List<Users> users = query.getResultList();
            int total = users.size();
            users = users.stream().skip((long) page * size).limit(size).toList();

            if(users.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(new Response(
                        Status.SUCCESS,
                        Constants.DATA_NOT_FOUND_CODE,
                        messageSource.getMessage(Constants.NO_RECORD_FOUND, null, locale),
                        new ArrayList<>()
                ));
            }
            PaginateDto dto = getPaginateDto(users, total, size, request.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(new Response(
                    Status.SUCCESS,
                    Constants.SUCCESS_CODE,
                    messageSource.getMessage(Constants.SUCCESS, null, locale),
                    dto
            ));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error found in searchPatient api : {}", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.SOMETHING_WENT_WRONG, null, locale),
                    new ArrayList<>()
            ));
        }
    }

    private PaginateDto getPaginateDto(List<Users> users, int total, int size, int id) {
        List<SearcPatientResponse> responseList = new ArrayList<>();

        for(Users s : users){
            SearcPatientResponse response = new SearcPatientResponse();
            response.setPatientName(s.getFirstName() + " " + s.getLastName());
            response.setContactNumber((s.getCountryCode() == null ? "":s.getCountryCode()) + s.getContactNumber());
            if(s.getCreatedBy() != null && s.getCreatedBy().equals(id)) response.setEdit(true);

            responseList.add(response);
        }
        PaginateDto dto = new PaginateDto();
        dto.setContent(responseList);
        dto.setSize(size);
        dto.setNoOfElements(responseList.size());
        dto.setTotalPages(total / size);
        dto.setTotalElements(total);
        return dto;
    }

    private static StringBuilder getStringBuilder(SearchPatientRequest request) {
        StringBuilder sb = new StringBuilder("Select u From Users u Where u.type = 'Patient' ");

        //search  by name
        if(request.getPatientName() != null && !request.getPatientName().isEmpty()){
            sb.append(" AND (u.firstName Like '%" + request.getPatientName().trim() +"%' OR (u.lastName LIKE '%" + request.getPatientName().trim() + "%') ");
        }
        //search  by contact number
        if(request.getContactNumber() != null && !request.getContactNumber().isEmpty()){
            sb.append(" AND u.contactNumber Like '%" + request.getContactNumber().trim() +"%'");
        }
        sb.append(" Order By u.userId DESC ");
        return sb;
    }

    public ResponseEntity<Response> createPatient(CreateAndEditPatientRequest request, Locale locale) {
        if(!request.isTermsAndCondition()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.TERMS_AND_CONDITION, null, locale)
            ));
        }

        Users patient = usersRepository.findByContactNumber(request.getContactNumber().trim()).orElse(null);
        if(patient != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(
                    Status.FAILED,
                    Constants.CONFLICT_CODE,
                    messageSource.getMessage(Constants.CONTACT_NUMBER_EXIST, null, locale)
            ));
        }
        Users user = new Users();
        user = createAndEditUser(request, user);

        Users users = usersRepository.findById(request.getUserId()).orElse(null);
        Map<String, Object> res = new HashMap<>();
        res.put("patientId", user.getUserId());
        if(users != null) res.put("createdBy", users.getFirstName() + " " + users.getLastName());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PATIENT_CREATE_SUCCESSFULLY, null, locale),
                res
        ));
    }

    private Users createAndEditUser(CreateAndEditPatientRequest request, Users users) {
        String[] fullName = request.getFullName().trim().split(" ");
        StringBuilder sb = new StringBuilder();
        if(fullName.length > 1){
            for(int i = 1 ; i < fullName.length ; i++){
                sb.append(fullName[i] + " ");
            }
        }

        users.setFirstName(fullName[0]);
        users.setLastName(sb.toString().trim());
        users.setEmail(request.getEmailId() == null || request.getEmailId().isEmpty() ? "" : request.getEmailId());
        users.setContactNumber(request.getContactNumber());
        users.setCountryCode(countryCode);
        Country country = countryRepository.findById(request.getCountryId() == null ? 0 : request.getCountryId()).orElse(null);
        users.setCountry(country);
        users.setState(request.getStateId());
        users.setCity(request.getCityId());
        users.setGender(request.getGender() == null || request.getGender().isEmpty() ? "" : request.getGender());
        users.setNotificationLanguage(request.getNotificationLanguage() == null || request.getNotificationLanguage().isEmpty() ? "" : request.getNotificationLanguage());
        users.setDob(request.getDob());
        users.setResidenceAddress(request.getResidentAddress().trim());
        users.setCreatedBy(request.getUserId());

        if(request.getPhoto() != null){
            //TODO : store the file name
//            publicService.storeFile(request.getPhoto(), "UserProfile/");
            users.setProfilePicture(request.getPhoto().getOriginalFilename());
        }else users.setProfilePicture("");

        users.setType(UserType.Patient);
        users.setStatus(StatusAI.A);
        users.setHospitalId(0);
        users.setOtpCounter(0);
        users.setAttemptCounter((short)0);
        users.setIsSuspended(0);
        users.setIsHpczVerified("Yes");
        users.setIsHospitalVerified("Yes");
        users.setApprovedBy(0);
        users.setCreatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.of(zone))));
        users.setIsInternational(YesNo.No);

        return usersRepository.save(users);
    }

    public ResponseEntity<Response> getPatient(Integer patientId, Locale locale) {
        Users patient = usersRepository.findById(patientId).orElseThrow(() -> new UsernameNotFoundException("Patient not found"));

        PatientResponse response = getPatientResponseDto(patient);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.SUCCESS, null, locale),
                response
        ));
    }

    private PatientResponse getPatientResponseDto(Users patient) {
        PatientResponse response = new PatientResponse();
        response.setPatientId(patient.getUserId());
        response.setFullName(patient.getFullName());
        response.setEmailId(StringUtils.isEmpty(patient.getEmail()) ? "" : patient.getEmail());
        response.setContactNumber(patient.getContactNumber());
        response.setCountryId(patient.getCountry() == null ? 0 : patient.getCountry().getId());
        response.setCountryName(patient.getCountry() == null ? "" : patient.getCountry().getName());

        State state = stateRepository.findById(patient.getState() == null ? 0 : patient.getState()).orElse(null);
        if(state != null) {
            response.setProvinceId(state.getId());
            response.setProvinceName(state.getName());
        }
        City city = cityRepository.findById(patient.getCity() == null ? 0 : patient.getCity()).orElse(null);
        if(city != null){
            response.setCityId(city.getId());
            response.setCityName(city.getName());
        }

        response.setGender(patient.getGender());
        response.setNotificationLanguage(patient.getNotificationLanguage());
        response.setDob(patient.getDob());
        response.setResidentialAddress(patient.getResidenceAddress());
        response.setPhoto(""); //TODO : implementation remaining
        return response;
    }

    public ResponseEntity<Response> updatePatient(Integer id, CreateAndEditPatientRequest request, Locale locale) {
        Users patient = usersRepository.findById(id).orElse(null);
        if(patient == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(
                    Status.FAILED,
                    Constants.BLANK_DATA_GIVEN_CODE,
                    messageSource.getMessage(Constants.NO_RECORD_FOUND, null, locale)
            ));
        }
        patient = createAndEditUser(request, patient);

        PatientResponse response = getPatientResponseDto(patient);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(
                Status.SUCCESS,
                Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PATIENT_UPDATED_SUCCESSFULLY, null, locale),
                response
        ));
    }
}
