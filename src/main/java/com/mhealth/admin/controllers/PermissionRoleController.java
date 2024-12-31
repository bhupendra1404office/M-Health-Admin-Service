package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.request.PermissionRoleRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.PermissionRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Permission Roles", description = "APIs for managing permission roles")
@RequestMapping("/api/v1/admin/permission-roles")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class PermissionRoleController {

    @Autowired
    private PermissionRoleService permissionRoleService;

    @Operation(summary = "Add a new permission role", responses = {
            @ApiResponse(responseCode = "200", description = "Permission role added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Permission role already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addPermissionRole(
            @Valid @RequestBody PermissionRoleRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "en") Locale locale) {
        return permissionRoleService.addPermissionRole(request, locale);
    }

    @Operation(summary = "Update an existing permission role", responses = {
            @ApiResponse(responseCode = "200", description = "Permission role updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Permission role not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updatePermissionRole(
            @PathVariable Integer id,
            @Valid @RequestBody PermissionRoleRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "en") Locale locale) {
        return permissionRoleService.updatePermissionRole(id, request, locale);
    }

    @Operation(summary = "Fetch all permission roles", responses = {
            @ApiResponse(responseCode = "200", description = "Permission roles fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Response> fetchPermissionRoles(
            @RequestHeader(name = "X-localization", required = false, defaultValue = "en") Locale locale) {
        return permissionRoleService.fetchPermissionRoles(locale);
    }
}
