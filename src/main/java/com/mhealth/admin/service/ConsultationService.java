package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.consultationDto.*;
import com.mhealth.admin.dto.enums.NotificationType;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.request.RescheduleRequest;
import com.mhealth.admin.dto.request.ViewConsultationRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.dto.response.ViewConsultationResponse;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.*;
import com.mhealth.admin.repository.*;
import com.mhealth.admin.sms.SMSApiService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static com.mhealth.admin.config.Constants.RESCHEDULE_CREATED_SUCCESS;
import static com.mhealth.admin.constants.Constants.*;

@Service
@Slf4j
public class ConsultationService {
    @Autowired
    private SlotMasterRepository slotMasterRepository;
    @Autowired
    private ConsultationRepository consultationRepository;
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
    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Autowired
    private SMSApiService smsApiService;
    @Value("${m-health.country}")
    private String mHealthCountry;


    public ResponseEntity<Response> searchPatient(SearchPatientRequest request, Locale locale) {
        log.info("Entering into searchPatient api : {}", request);

        StringBuilder sb = getStringBuilder(request);

        //generate Query
        int size = request.getSize()== null ? Constants.DEFAULT_PAGE_SIZE : request.getSize();
        Query query = entityManager.createQuery(sb.toString(), Users.class);
        List<Users> users = query.getResultList();
        int total = users.size();
        users = users.stream().skip((long) request.getPage() * size).limit(size).toList();

        if(users.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new Response(
                    Status.FAILED,
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
        users.setEmail(StringUtils.isEmpty(request.getEmailId()) ? "" : request.getEmailId());
        users.setContactNumber(request.getContactNumber());
        users.setCountryCode(countryCode);
        Country country = countryRepository.findById(request.getCountryId() == null ? 0 : request.getCountryId()).orElse(null);
        users.setCountry(country);
        users.setState(request.getStateId());
        users.setCity(request.getCityId());
        users.setGender(StringUtils.isEmpty(request.getGender()) ? "" : request.getGender());
        users.setNotificationLanguage(StringUtils.isEmpty(request.getNotificationLanguage()) ? "" : request.getNotificationLanguage());
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

    public ResponseEntity<Response> rescheduleAppointment(RescheduleRequest request, Locale locale) {
        Response responseDto = new Response();

        Consultation consultation = consultationRepository.findById(request.getCaseId()).orElseThrow(() -> new AdminModuleExceptionHandler("Case not found"));
        SlotMaster master = slotMasterRepository.findById(request.getSlotId()).orElseThrow(() -> new AdminModuleExceptionHandler("Slot not found"));

        SlotMaster oldSlotMaster = slotMasterRepository.findById(consultation.getSlotId().getSlotId()).orElseThrow(() -> new AdminModuleExceptionHandler("Slot not found"));
        String oldTime = consultation.getConsultationDate() + ", " + oldSlotMaster.getSlotStartTime();
        String newTime = request.getConsultationDate() + ", " + master.getSlotStartTime();
        consultation.setConsultationDate(request.getConsultationDate());
        consultation.setSlotId(master);
        consultation.setUpdatedAt(LocalDateTime.now(ZoneId.of(zone)));

        if(!request.getMessages().isEmpty()) consultation.setMessage(request.getMessages());

        consultationRepository.save(consultation);

        //notification
        Users patient = usersRepository.findByUserIdAndType(consultation.getPatientId().getUserId(), UserType.Patient);

        Users doctor = usersRepository.findByUserIdAndType(consultation.getDoctorId().getUserId(), UserType.Doctor);

        //TODO: send doctor notification for rescheduling of the time slot

        if(!org.apache.commons.lang.StringUtils.isEmpty(patient.getNotificationLanguage()) && patient.getNotificationLanguage().equalsIgnoreCase("en")){
            locale = Locale.ENGLISH;
        }
        else locale = new Locale(mHealthCountry.toLowerCase());
        String patientMessage = getMessage(RESCHEDULE_CREATED_SUCCESS, locale, patient.getFullName(), oldTime, doctor.getFullName(), newTime);
        publicService.addNewNotificationMessage(patientMessage, patient, request.getUserId(), NotificationType.Consult, consultation.getCaseId());
        log.info("Message : {}", patientMessage);
        if(smsSent){
            String patientNumber = "+"+patient.getCountryCode()+patient.getContactNumber();
            smsApiService.sendMessage(patientNumber, patientMessage, mHealthCountry);
        }

        responseDto.setMessage(patientMessage);
        responseDto.setData(consultation.getSlotId());
        responseDto.setStatus(Status.SUCCESS);
        responseDto.setCode(SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    protected String getMessage(String id, Locale locale, Object... args) {
        return messageSource.getMessage(id, args, locale);
    }

    public static int calculateAge(LocalDate dateOfBirth) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference between current date and date of birth
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    /**
     * Generic function to split and format a time range string.
     * @param timeRange The input string in the format "HH:mm:HH:mm".
     * @return A formatted string in the format "HH:mm To HH:mm".
     */
    public static String formatTimeRange(String timeRange) {
        if (timeRange == null || !timeRange.matches("\\d{2}:\\d{2}:\\d{2}:\\d{2}")) {
            return "Invalid input format. Expected 'HH:mm:HH:mm'.";
        }

        // Split the input string by ":"
        String[] timeParts = timeRange.split(":");

        // Construct start and end times
        String startTime = timeParts[0] + ":" + timeParts[1];
        String endTime = timeParts[2] + ":" + timeParts[3];

        // Return formatted string
        return startTime + " To " + endTime;
    }


    public ResponseEntity<PaginationResponse<ViewConsultationResponse>> openActiveConsultation(@Valid ViewConsultationRequest request, Locale locale) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize() != null ? request.getSize() : Constants.DEFAULT_PAGE_SIZE);
        Page<Consultation> page = getFilteredConsultations(request.getPatientName(), request.getPhoneNumber(), request.getConsultationDate(), request.getCaseId(), pageable);

        List<ViewConsultationResponse> dtoList = new ArrayList<>();
        page.getContent().forEach(ele -> {
            ViewConsultationResponse dto = new ViewConsultationResponse();
            dto.setCaseId(ele.getCaseId());
            dto.setPatientName(ele.getPatientId().getFullName());
            dto.setPatientContactNo(ele.getPatientId().getContactNumber());
            dto.setConsultationDate(ele.getConsultationDate());
            dto.setDoctorName(ele.getDoctorId().getFullName());
            dto.setDoctorContactNo(ele.getDoctorId().getFullName());
            dto.setPatientAge(calculateAge(ele.getPatientId().getDob()) + " years");
            dto.setPatientAddress(ele.getPatientId().getResidenceAddress());
            dto.setConsultationTime(ele.getSlotId().getSlotTime());
            dto.setDoctorCharge(ele.getDoctorId().getTotalMoney());
            dto.setAdminCommission(0.0);
            dto.setFinalPrice(dto.getDoctorCharge() + dto.getAdminCommission());
            dto.setStatus(ele.getConsultStatus().toString());
            dtoList.add(dto);
        });

        return ResponseEntity.ok(new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.HEALTH_TIP_FETCHED_SUCCESS, null, locale),
                dtoList, page.getTotalElements(), (long) page.getSize(), (long) page.getNumber()));
    }

    public Page<Consultation> getFilteredConsultations(String fullName, String contactNumber, LocalDate consultationDate, Integer caseId, Pageable pageable) {
        Specification<Consultation> specification = filterByParams(fullName, contactNumber, consultationDate, caseId);
        return consultationRepository.findAll(specification, pageable);
    }

    public static Specification<Consultation> filterByParams(String fullName, String contactNumber, LocalDate consultationDate, Integer caseId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by consultationDate
            if (consultationDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("consultationDate"), consultationDate));
            }

            // Filter by caseId
            if (caseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("caseId"), caseId));
            }

            // Filter by fullName and contactNumber
            if (fullName != null || contactNumber != null) {
                Join<Consultation, Users> patient = root.join("patientId");

                if (fullName != null) {
                    Predicate namePredicate = criteriaBuilder.or(
                            criteriaBuilder.like(patient.get("firstName"), "%" + fullName + "%"),
                            criteriaBuilder.like(patient.get("lastName"), "%" + fullName + "%"),
                            criteriaBuilder.like(criteriaBuilder.concat(
                                            criteriaBuilder.concat(patient.get("firstName"), " "),
                                            patient.get("lastName")),
                                    "%" + fullName + "%"
                            )
                    );
                    predicates.add(namePredicate);
                }

                if (contactNumber != null) {
                    predicates.add(criteriaBuilder.equal(patient.get("contactNumber"), contactNumber));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
