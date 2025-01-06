package com.mhealth.admin.dto.response;

import com.mhealth.admin.dto.dto.PermissionDto;
import com.mhealth.admin.model.PermissionRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRoleDto {
    private List<PermissionDto> permissions;
    private PermissionRole role;
}
