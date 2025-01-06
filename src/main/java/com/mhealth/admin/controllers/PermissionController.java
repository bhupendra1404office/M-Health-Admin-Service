package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.PermissionRequest;
import com.mhealth.admin.dto.request.SavePermissionRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Permission;
import com.mhealth.admin.service.PermissionService;
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
@Tag(name = "Permissions", description = "APIs for managing permissions")
@RequestMapping("/api/v1/admin/permission")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Add a new permission", responses = {
            @ApiResponse(responseCode = "200", description = "Permission added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Permission already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addPermission(
            @Valid @RequestBody PermissionRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return permissionService.addPermission(request, locale);
    }

    @Operation(summary = "Update an existing permission", responses = {
            @ApiResponse(responseCode = "200", description = "Permission updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updatePermission(
            @PathVariable Integer id,
            @Valid @RequestBody PermissionRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return permissionService.updatePermission(id, request, locale);
    }

    @Operation(summary = "Fetch permissions tree", responses = {
            @ApiResponse(responseCode = "200", description = "Permissions fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Response> fetchPermissions(
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return permissionService.fetchPermission(locale);
    }
}
