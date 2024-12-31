package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.NurseServiceRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.NurseServiceService;
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
@Tag(name = "Nurse Service Management", description = "APIs for managing nurse services")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/nurse-services")
public class NurseServiceController {
    @Autowired
    private NurseServiceService service;

    @Operation(summary = "Create a new nurse service", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> createNurseService(
            @Valid @RequestBody NurseServiceRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.createNurseService(request, locale);
    }

    @Operation(summary = "Update an existing nurse service", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Nurse service not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateNurseService(
            @PathVariable Integer id,
            @Valid @RequestBody NurseServiceRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateNurseService(id, request, locale);
    }

    @Operation(summary = "Get all nurse services", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @GetMapping
    public ResponseEntity<Response> getAllNurseServices(
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getAllNurseServices(locale);
    }

    @Operation(summary = "Get nurse service by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Nurse service not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getNurseServiceById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getNurseServiceById(id, locale);
    }

    @Operation(summary = "Delete nurse service by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Nurse service not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteNurseServiceById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteNurseServiceById(id, locale);
    }

    @Operation(summary = "Search nurse services by name and status", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "No records found", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Response> searchNurseServices(
            @RequestParam(required = false) String seviceName,
            @RequestParam(required = false) String status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchNurseServices(seviceName, status, locale);
    }

    @Operation(summary = "Update the status of a nurse service", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Nurse service not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> updateNurseServiceStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateNurseServiceStatus(id, status, locale);
    }
}
