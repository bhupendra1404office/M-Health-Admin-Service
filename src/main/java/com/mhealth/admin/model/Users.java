package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.Classification;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.enums.YesNo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mh_users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "hospital_id")
    private Integer hospitalId = 0;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private UserType type = UserType.Doctor;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusAI status = StatusAI.I;

    @Column(name = "slot_type_id")
    private Integer slotTypeId =2; // have to put by-default

    @Column(name = "otp")
    private Integer otp;

    @Column(name = "otp_time")
    private Timestamp otpTime;

    @Column(name = "otp_counter")
    private Integer otpCounter = 0;

    @Column(name = "attempt_counter")
    private Short attemptCounter =0;

    @Column(name = "is_suspended")
    private Integer isSuspended = 0;

    @Column(name = "has_app")
    private String hasApp = "No";

    @Column(name = "is_verified")
    private String isVerified = "No";

    @Column(name = "language_fluency")
    private String languageFluency;

    @Column(name = "is_hpcz_verified")
    private String isHpczVerified = "Yes"; //required

    @Column(name = "is_hospital_verified")
    private String isHospitalVerified = "Yes";//required

    @Column(name = "hpcz_approver_status")
    private String hpczApproverStatus = "pandding";

    @Column(name = "hospital_approver_status")
    private String hospitalApproverStatus = "pandding";

    @Column(name = "approved_by")
    private Integer approvedBy = 0;

    @Column(name = "agent_user_id")
    private Integer agentUserId;

    @Column(name = "administrator_name")
    private String administratorName;

    @Column(name = "administrator_mobile_number")
    private String administratorMobileNumber;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "experience")
    private Float experience;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "usr_reference_number")
    private String usrReferenceNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "has_doctor_video")
    private String hasDoctorVideo;

    @Column(name = "notification_language")
    private String notificationLanguage;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "wallet_id")
    private BigInteger walletId;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "passing_year")
    private Integer passingYear;

    @Column(name = "specialization_id")
    private Integer specializationId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "hospital_address")
    private String hospitalAddress;

    @Column(name = "residence_address")
    private String residenceAddress;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "doctor_classification")
    private String doctorClassification;

    @Column(name = "professional_identification_number")
    private String professionalIdentificationNumber;

    @Column(name = "extra_activities")
    private String extraActivities;

    private LocalDate dob;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "state_id")
    private Integer state;

    @Column(name = "city_id")
    private Integer city;

    @Column(name = "classification")
    @Enumerated(EnumType.STRING)
    private Classification classification;

    @Column(name = "is_international")
    @Enumerated(EnumType.STRING)
    private YesNo isInternational;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
