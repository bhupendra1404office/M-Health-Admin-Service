package com.mhealth.admin.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Universal APIs", description = "Universal APIs")
@RequestMapping("/api/v1/admin/site")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class SiteController {

}
