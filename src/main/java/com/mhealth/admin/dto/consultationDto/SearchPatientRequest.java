package com.mhealth.admin.dto.consultationDto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchPatientRequest {
    @NotNull(message = "UserId is required")
    private Integer userId;

    private String patientName;
    private String contactNumber;

    @NotNull(message = "Page number is required")
    @Min(value = 1, message = "Page number must be 1 or greater")
    private Integer page;

    private Integer size = 10;
}
