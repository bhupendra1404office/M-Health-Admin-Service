package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.SlotTypeRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.SlotTypeService;
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
@Tag(name = "Slot Type Management", description = "APIs for managing slot types")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/slot-types")
public class TimeSlotController {
    @Autowired
    private SlotTypeService service;

    @Operation(summary = "Create a new slot type", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> createSlotType(
            @Valid @RequestBody SlotTypeRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.createSlotType(request, locale);
    }

    @Operation(summary = "Update an existing slot type", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Slot type not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateSlotType(
            @PathVariable Integer id,
            @Valid @RequestBody SlotTypeRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateSlotType(id, request, locale);
    }

    @Operation(summary = "Get all slot types", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @GetMapping
    public ResponseEntity<Response> getAllSlotTypes(
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getAllSlotTypes(locale);
    }

    @Operation(summary = "Get slot type by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Slot type not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getSlotTypeById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getSlotTypeById(id, locale);
    }

    @Operation(summary = "Delete slot type by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Slot type not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteSlotTypeById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteSlotTypeById(id, locale);
    }
}
