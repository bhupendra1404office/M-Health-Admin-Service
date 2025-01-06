package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.GlobalConfigurationRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.GlobalConfigurationService;
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
@Tag(name = "Global Configuration", description = "API for managing global configurations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/configurations")
public class GlobalConfigurationController {

    @Autowired
    private GlobalConfigurationService service;

    @Operation(summary = "Create a new configuration", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, description = "Successfully created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> createConfiguration(
            @Valid @RequestBody GlobalConfigurationRequest request,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.createConfiguration(request,locale);
    }

    @Operation(summary = "Update an existing configuration", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, description = "Successfully updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateConfiguration(
            @PathVariable Integer id,
            @Valid @RequestBody GlobalConfigurationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.updateConfiguration(id, request, locale);
    }

    @Operation(summary = "Search global configuration by key and value", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, content = @Content(schema = @Schema(implementation = Response.class))),
    })
    @PutMapping("/search")
    public ResponseEntity<Response> searchConfiguration(
            @Valid @RequestBody GlobalConfigurationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.searchConfiguration(request.getKey(),request.getValue(),locale);
    }

    @Operation(summary = "Get all configurations", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @GetMapping
    public ResponseEntity<Response> getAllConfigurations(
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.getAllConfigurations(locale);
    }

    @Operation(summary = "Get configuration by ID", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getConfigurationById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.getConfigurationById(id,locale);
    }

    @Operation(summary = "Delete configuration by ID", responses = {
            @ApiResponse(responseCode = Constants.SUCCESS_CODE, description = "Successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteConfiguration(@PathVariable Integer id,
                                                        @RequestHeader(name = "X-localization",
            required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.deleteConfigurationById(id,locale);
    }
}
