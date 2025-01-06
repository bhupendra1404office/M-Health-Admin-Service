package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.AppBannerRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.AppBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
@RestController
@Tag(name = "App Banner Management", description = "APIs for managing app banners")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/app-banners")
public class AppBannerController {

    @Autowired
    private AppBannerService service;

    @PostMapping
    @Operation(summary = "Create a new app banner")
    public ResponseEntity<Response> createAppBanner(@Valid @RequestBody AppBannerRequest request,
                                                    @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.createAppBanner(request,locale);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing app banner")
    public ResponseEntity<Response> updateAppBanner(
            @PathVariable Integer id,
            @Valid @RequestBody AppBannerRequest request,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateAppBanner(id, request,locale);
    }

    @GetMapping
    @Operation(summary = "Get all app banners")
    public ResponseEntity<Response> getAllAppBanners(
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getAllAppBanners(locale);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get app banner by ID")
    public ResponseEntity<Response> getAppBannerById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getAppBannerById(id,locale);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete app banner by ID")
    public ResponseEntity<Response> deleteAppBannerById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteAppBannerById(id,locale);
    }

    @GetMapping("/search")
    @Operation(summary = "Search app banners by name")
    public ResponseEntity<Response> searchAppBanners(
            @RequestParam String iname,
            @RequestHeader(name = "X-localization", required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchAppBanners(iname,locale);
    }
}
