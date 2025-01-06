package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.request.MobileReleaseRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.MobileReleaseService;
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
@Tag(name = "Mobile Release Management", description = "APIs for managing mobile releases")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/mobile-releases")
public class MobileReleaseController {

    @Autowired
    private MobileReleaseService service;

    @Operation(summary = "Create a new mobile release", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> createMobileRelease(
            @Valid @RequestBody MobileReleaseRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.createMobileRelease(request, locale);
    }

    @Operation(summary = "Update an existing mobile release", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Mobile release not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateMobileRelease(
            @PathVariable Integer id,
            @Valid @RequestBody MobileReleaseRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.updateMobileRelease(id, request, locale);
    }

    @Operation(summary = "Get mobile release by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Mobile release not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getMobileReleaseById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.getMobileReleaseById(id, locale);
    }


    @Operation(summary = "Search mobile release by app version", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "No records found", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Response> searchMobileRelease(
            @RequestParam(required = false) String appVersion,
            @RequestParam(defaultValue = "0") int page, // Default to page 0
            @RequestParam(defaultValue = "10") int size, // Default to 10 records per page
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.searchMobileReleaseByAppVersion(appVersion, locale, page, size);
    }

}
