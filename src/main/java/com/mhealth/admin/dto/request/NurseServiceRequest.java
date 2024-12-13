package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NurseServiceRequest {
    @NotBlank(message = "Service name is mandatory")
    private String seviceName;

    private String serviceImage;

    @NotNull(message = "Service price is mandatory")
    private Float servicePrice;

    private String seviceNameSl;

    private String descriptionSl;

    @NotNull(message = "Admin commission is mandatory")
    private Float adminCommission;

    @NotNull(message = "Total service price is mandatory")
    private Float totalServicePrice;

    @NotBlank(message = "Commission type is mandatory")
    private String commissionType;

    @NotBlank(message = "Status is mandatory")
    private String status;

    @NotBlank(message = "Description is mandatory")
    private String description;
}
