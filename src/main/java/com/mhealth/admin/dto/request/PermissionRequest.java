package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    private Integer id;
    @NotNull(message = "Code is required")
    @NotBlank(message = "Code is required")
    private String code;
    private int level;
    private Integer parentId;
}
