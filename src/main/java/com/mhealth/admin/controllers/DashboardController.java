package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Dashboard Controller", description = "APIs for Dashboard statistics and details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/details")
    public ResponseEntity<?> getDashboardDetails(@RequestHeader(name = "X-localization",
            required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return dashboardService.getDashboardDetails(locale);
    }
}
