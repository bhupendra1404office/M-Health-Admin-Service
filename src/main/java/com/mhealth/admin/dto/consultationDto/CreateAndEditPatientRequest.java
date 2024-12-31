package com.mhealth.admin.dto.consultationDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateAndEditPatientRequest {
    @NotNull(message = "User Id is required")
    private Integer userId;

    private MultipartFile photo;

    @NotNull(message = "Full name is required")
    private String fullName;

    private String emailId;

    @NotNull(message = "Contact Number is required")
    private String contactNumber;
    private Integer countryId;
    private Integer stateId;
    private Integer cityId;
    private String gender;
    private String notificationLanguage;

    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @NotNull(message = "Resident Address is required")
    private String residentAddress;

    @NotNull(message = "Terms and condition should be checked")
    private boolean termsAndCondition;
}
