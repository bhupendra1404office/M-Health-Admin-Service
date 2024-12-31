package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.dto.VerifyLoginOtp;
import com.mhealth.admin.dto.request.LoginRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Dashboard Controller", description = "APIs for Dashboard statistics and details")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(method = "POST",description = "Login api")
    public ResponseEntity<Response> login(@RequestBody @Validated LoginRequest request,
                                          @RequestHeader(name = "X-localization",
                                                  required = false,defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return authService.login(request,locale);
    }
    /*
       Get otp on Forgot-password by providing contact number
    */
    @GetMapping("/forgot-password/{contactNumber}")
    public ResponseEntity<?> generateOtpForForgotPassword(@PathVariable String contactNumber,
                                                          @RequestHeader(name = "X-localization",
                                                                  required = false,defaultValue = "so") Locale locale) {
        log.info("Entering into generateOtpForForgotPassword : {}", contactNumber);
        return authService.generateOtpForForgotPassword(contactNumber, locale);
    }
    /*
        Verify - otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyLoginOtp request,
                                       @RequestHeader(name = "X-localization",
                                               required = false,defaultValue = "so") Locale locale) {
        log.info("Entering in verifyOtp: {}", request);
        return authService.verifyOtpForForgotPassword(request, locale);
    }
}
