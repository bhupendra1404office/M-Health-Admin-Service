package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientResponse {
    private Integer patientId;
    private String fullName;
    private String emailId;
    private String contactNumber;
    private Integer countryId;
    private String countryName;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private String gender;
    private String notificationLanguage;
    private LocalDate dob;
    private String residentialAddress;
    private String photo;
}
