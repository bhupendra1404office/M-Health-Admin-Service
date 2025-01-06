package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.PermissionDto;
import com.mhealth.admin.dto.request.PermissionRequest;
import com.mhealth.admin.dto.request.SavePermissionRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Permission;
import com.mhealth.admin.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MessageSource messageSource;

    public List<PermissionDto> getTreeStructure(List<Permission> firstLevel,List<Permission>second,List<Permission>third){
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

    public ResponseEntity<Response> fetchPermission(Locale locale) {
        log.info("ENTERING fetchOrders");
        List<Permission> firstLevel = permissionRepository.findByLevel(0);
        List<Permission> second = permissionRepository.findByLevel(1);
        List<Permission> third = permissionRepository.findByLevel(2);

        List<PermissionDto> data = getTreeStructure(firstLevel,second,third);

        Response response = new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSIONS_FETCHED, null, locale),data);
        log.info("EXITING fetchOrders");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Response> addPermission(PermissionRequest request, Locale locale) {
        if (request.getId() != null && permissionRepository.findById(request.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(Status.FAILED, Constants.CONFLICT_CODE,
                            messageSource.getMessage(Constants.PERMISSION_EXISTS, null, locale)));
        }

        Permission parentPermission = null;
        if (request.getParentId() != null) {
            parentPermission = permissionRepository.findById(request.getParentId()).orElse(null);
            if (parentPermission == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                                messageSource.getMessage(Constants.PARENT_NOT_FOUND, null, locale)));
            }
        }

        Permission permission = new Permission();
        permission.setCode(request.getCode());
        permission.setLevel(request.getLevel());
        permission.setParent(parentPermission);

        permissionRepository.save(permission);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ADDED, null, locale), permission));
    }

    public ResponseEntity<Response> updatePermission(Integer id, PermissionRequest request, Locale locale) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if (permission == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                            messageSource.getMessage(Constants.PERMISSION_NOT_FOUND, null, locale)));
        }

        Permission parentPermission = null;
        if (request.getParentId() != null) {
            parentPermission = permissionRepository.findById(request.getParentId()).orElse(null);
            if (parentPermission == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                                messageSource.getMessage(Constants.PARENT_NOT_FOUND, null, locale)));
            }
        }

        permission.setCode(request.getCode());
        permission.setLevel(request.getLevel());
        permission.setParent(parentPermission);

        permissionRepository.save(permission);

        return ResponseEntity.ok(new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_UPDATED, null, locale), permission));
    }

}
