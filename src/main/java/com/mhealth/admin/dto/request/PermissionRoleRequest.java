package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionRoleRequest {

    @NotNull
    private UserType roleType;

    @NotNull
    private String permissions;
}
