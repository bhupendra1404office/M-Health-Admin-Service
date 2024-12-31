package com.mhealth.admin.dto.consultationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearcPatientResponse {
    private String patientName;
    private String contactNumber;
    private boolean isEdit;
}
