package com.mhealth.admin.dto.dto;

import com.mhealth.admin.dto.response.PermissionRoleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String doctorId;
    private String token;
    private boolean isInternational;
    private PermissionRoleDto permissions;

    public LoginResponseDto(String doctorId,String token,boolean isInternational){
        this.doctorId = doctorId;
        this.token = token;
        this.isInternational = isInternational;
    }
}