package com.example.mtsimregister.service;

import com.example.mtsimregister.helper.CsvHelper;
import com.example.mtsimregister.model.RegistrationInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    CsvHelper csvHelper;

    public List<RegistrationInfo> processBulKRegistration(MultipartFile file, Logger logger) {

        if (!csvHelper.isCSVFile(file)) {
            throw new IllegalArgumentException("Not a csv file. Please upload a csv file");
        }

        InputStream inputStream;

        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("File is corrupted!");
        }

        return csvHelper.processBulkRegistration(inputStream, logger);

    }
}
