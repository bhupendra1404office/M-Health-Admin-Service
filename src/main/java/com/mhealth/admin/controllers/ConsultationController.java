package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.consultationDto.CreateAndEditPatientRequest;
import com.mhealth.admin.dto.consultationDto.SearchPatientRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Consultation Controller", description = "APIs for Consultations statistics and details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/consultation")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;

    /*
        List of patient
     */
    @PostMapping("/search-patient")
    @Operation(method = "POST",description = "search patient api")
    public ResponseEntity<Response> searchPatient(@RequestBody SearchPatientRequest request,
                                          @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.searchPatient(request,locale);
    }
    /*
        Create And Edit Patient
     */
    @PostMapping
    @Operation(method = "POST",description = "Create patient api")
    public ResponseEntity<Response> createPatient(@Valid @ModelAttribute CreateAndEditPatientRequest request,
                                                  @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.createPatient(request,locale);
    }

    /*
        Getting Patient response
     */
    @GetMapping("/{patientId}")
    @Operation(method = "GET",description = "Get details of individual patient by patient Id")
    public ResponseEntity<Response> getPatient(@PathVariable Integer patientId,
                                               @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.getPatient(patientId, locale);
    }
    /*
        Updating Patient
     */
    @PutMapping("/{id}")
    @Operation(method = "PUT",description = "Update patient")
    public ResponseEntity<Response> updatePatient(@PathVariable Integer id,
                                                  @ModelAttribute CreateAndEditPatientRequest request,
                                                  @RequestHeader(name = "X-localization", required = false,defaultValue = "so") Locale locale) {
        return consultationService.updatePatient(id, request, locale);
    }
}
