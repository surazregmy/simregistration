package com.example.mtsimregister.controller;

import com.example.mtsimregister.model.RegistrationInfo;
import com.example.mtsimregister.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    RegistrationService registrationService;

    @PostMapping("/bulk")
    public ResponseEntity<List<RegistrationInfo>> bulkRegister(@RequestParam("file") MultipartFile file) {
        List<RegistrationInfo> registrationInfoList = registrationService.processBulKRegistration(file, logger);
        return ResponseEntity.ok(registrationInfoList);
    }
}
