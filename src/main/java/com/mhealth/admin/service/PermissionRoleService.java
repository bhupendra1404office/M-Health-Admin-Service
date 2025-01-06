package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.PermissionDto;
import com.mhealth.admin.dto.request.PermissionRoleRequest;
import com.mhealth.admin.dto.response.PermissionRoleDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Permission;
import com.mhealth.admin.model.PermissionRole;
import com.mhealth.admin.repository.PermissionRepository;
import com.mhealth.admin.repository.PermissionRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class PermissionRoleService {

    @Autowired
    private PermissionRoleRepository permissionRoleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PermissionRepository permissionRepository;

    public ResponseEntity<Response> addPermissionRole(PermissionRoleRequest request, Locale locale) {
        PermissionRole permissionRole = new PermissionRole();
        permissionRole.setRoleType(request.getRoleType());
        permissionRole.setPermissions(request.getPermissions());
        permissionRoleRepository.save(permissionRole);
        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_ADDED,null,locale), permissionRole));
    }

    public ResponseEntity<Response> updatePermissionRole(Integer id, PermissionRoleRequest request, Locale locale) {
        PermissionRole permissionRole = permissionRoleRepository.findById(id).orElse(null);
        if (permissionRole == null) {
            return ResponseEntity.status(404).body(new Response(
                    Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.PERMISSION_ROLE_NOT_FOUND,null,locale)));
        }

        permissionRole.setRoleType(request.getRoleType());
        permissionRole.setPermissions(request.getPermissions());
        permissionRoleRepository.save(permissionRole);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_UPDATED,null,locale),
                permissionRole));    }

    public ResponseEntity<Response> fetchPermissionRoles(Locale locale) {
        List<PermissionRoleDto> respose = new ArrayList<>();
        List<PermissionRole> roles = permissionRoleRepository.findAll();
        for(PermissionRole role:roles){

            List<String> rolesStringIds = Arrays.asList(role.getPermissions().split(","));
            List<Integer> ids = new ArrayList<>();
            for(String s:rolesStringIds){
                ids.add(Integer.parseInt(s));
            }
            List<Permission> permissionList = new ArrayList<>();
            if(!ids.isEmpty()){
                permissionList = permissionRepository.findByIds(ids);
            }

            List<Permission> level0 = new ArrayList<>();
            List<Permission> level1 = new ArrayList<>();
            List<Permission> level2 = new ArrayList<>();

            for(Permission p:permissionList){
                if(p.getLevel()==0){
                    level0.add(p);
                }else if(p.getLevel()==1){
                    level1.add(p);
                } else if (p.getLevel()==2) {
                    level2.add(p);
                }
            }

            List<PermissionDto> data = getTreeStructure(level0,level1,level2);
            respose.add(new PermissionRoleDto(data,role));
        }

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_FETCHED,null,locale),
                roles));
    }

    public List<PermissionDto> getTreeStructure(
            List<Permission> firstLevel,List<Permission>second,List<Permission>third){
        List<PermissionDto> response = new ArrayList<>();
        for(Permission p:firstLevel){
            response.add(new PermissionDto(p));
        }
        for(PermissionDto dto :response){
            for(Permission p:second){
                if(p.getParent()!= null && p.getParent().getId()==dto.getId()){
                    dto.getSubPermission().add(new PermissionDto(p));
                }
            }
        }
        for(PermissionDto external :response){
            for(PermissionDto internal :external.getSubPermission()){
                for(Permission p:third){
                    if(p.getParent()!=null && p.getParent().getId()==internal.getId()){
                        internal.getSubPermission().add(new PermissionDto(p));
                    }
                }
            }
        }

        return response;
    }
}

