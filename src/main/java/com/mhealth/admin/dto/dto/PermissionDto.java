package com.mhealth.admin.dto.dto;

import com.mhealth.admin.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    Integer id;
    String code;
    int level;
    List<PermissionDto> subPermission;

    public PermissionDto(Permission p){
        this.id = p.getId();
        this.code = p.getCode();
        this.level = p.getLevel();
        this.subPermission = new ArrayList<>();
    }
}
