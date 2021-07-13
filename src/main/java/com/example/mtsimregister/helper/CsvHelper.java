package com.example.mtsimregister.helper;

import com.example.mtsimregister.model.RegistrationInfo;
import com.example.mtsimregister.constant.DateUtils;
import com.example.mtsimregister.constant.Gender;
import com.example.mtsimregister.constant.SimType;
import com.example.mtsimregister.constant.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class CsvHelper {

    @Autowired
    Validator validator;

    public static String TYPE = "text/csv";
    static String[] HEADERS = {"MSISDN", "SIM_TYPE", "NAME", "DATE_OF_BIRTH", "GENDER", "ADDRESS", "ID_NUMBER"};

    public boolean isCSVFile(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public List<RegistrationInfo> processBulkRegistration(InputStream inputStream, Logger logger) {

        List<RegistrationInfo> validRegistrationInfoLists = new ArrayList<>();
        List<RegistrationInfo> duplicateRegistrationInfoLists = new ArrayList<>();
        Map<RegistrationInfo, Set<ConstraintViolation<RegistrationInfo>>> invalidRegistrationsAndReasonsMap = new HashMap<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            List<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {

                RegistrationInfo registrationInfo = new RegistrationInfo();
                String mSiSdn = csvRecord.get(HEADERS[0]);
                registrationInfo.setMsiSdn(mSiSdn);
                registrationInfo.setSimType(EnumUtils.getEnum(SimType.class, csvRecord.get(HEADERS[1])));
                registrationInfo.setName(csvRecord.get(HEADERS[2]));
                registrationInfo.setDateOfBirth(DateUtils.getDateFromString(csvRecord.get(HEADERS[3])));
                registrationInfo.setGender(EnumUtils.getEnum(Gender.class, csvRecord.get(HEADERS[4])));
                registrationInfo.setAddress(csvRecord.get(HEADERS[5]));
                registrationInfo.setIdNumber(csvRecord.get(HEADERS[6]));

                if (isAlreadyRegistered(validRegistrationInfoLists, mSiSdn)) {
                    registrationInfo.setStatus(Status.REJECTED);
                    duplicateRegistrationInfoLists.add(registrationInfo);
                    continue;
                }

                Set<ConstraintViolation<RegistrationInfo>> validationErrors = validator.validate(registrationInfo);
                if (validationErrors.size() == 0) {
                    registrationInfo.setStatus(Status.REGISTERED);
                    validRegistrationInfoLists.add(registrationInfo);
                } else {
                    registrationInfo.setStatus(Status.REJECTED);
                    invalidRegistrationsAndReasonsMap.put(registrationInfo, validationErrors);
                }
            }

            processValidRegistrations(validRegistrationInfoLists, logger);
            processInvalidRegistrations(invalidRegistrationsAndReasonsMap);
            processDuplicateRegistrations(duplicateRegistrationInfoLists);

        } catch (IOException e) {
            logger.warn("Could not read the file");

        }
        return validRegistrationInfoLists;
    }

    private void processDuplicateRegistrations(List<RegistrationInfo> duplicateRegistrationInfoLists) {
        if (duplicateRegistrationInfoLists.size() == 0) {
            System.out.println("No Duplicate Registration");
            return;
        }
        duplicateRegistrationInfoLists.forEach((duplicateRegistraion) -> {
            System.out.println("Registration failed for " + duplicateRegistraion.getMsiSdn() + ". " + "STATUS: " + duplicateRegistraion.getStatus());
            System.out.println(" >> Duplicate MSISDN");
        });
    }

    private boolean isAlreadyRegistered(List<RegistrationInfo> validRegistrationInfoLists, String mSiSdn) {
        return validRegistrationInfoLists.stream().
                anyMatch(validRegistraion
                        -> validRegistraion.getMsiSdn().equalsIgnoreCase(mSiSdn)
                );
    }

    private void processInvalidRegistrations(Map<RegistrationInfo, Set<ConstraintViolation<RegistrationInfo>>> invalidRegistrationAndReasonMap) {
        if (invalidRegistrationAndReasonMap.size() == 0) {
            System.out.println("No Invalid Registration");
            return;
        }
        invalidRegistrationAndReasonMap.forEach((key, value) -> {
            System.out.println("Registration failed for " + key.getMsiSdn() + ". " + "STATUS: " + key.getStatus());
            value.forEach(constraintViolation -> System.out.println(" >> " + constraintViolation.getMessage()));
        });
    }

    private void processValidRegistrations(List<RegistrationInfo> validRegistrationInfoLists, Logger logger) throws IOException {
        if (validRegistrationInfoLists.size() == 0) {
            System.out.println("No Valid Registration");
            return;
        }
        validRegistrationInfoLists.forEach(validRegistration -> {

            writeFile(validRegistration);
            consoleOutput(validRegistration);
            sendSMS(validRegistration, logger);

        });
    }

    private void consoleOutput(RegistrationInfo validRegistration) {
        System.out.println("Registration is successful for " + validRegistration.getMsiSdn() + ". " + "STATUS: " + validRegistration.getStatus());
    }

    private void sendSMS(RegistrationInfo validRegistration, Logger logger) {
        if (validRegistration.getGender() == Gender.F)
            logger.info(" >> SMS is sent to Female " + validRegistration.getMsiSdn());
        else
            logger.info(" >> SMS is sent to Male " + validRegistration.getMsiSdn());
    }

    private void writeFile(RegistrationInfo validRegistration) {
        try (
                FileWriter out = new FileWriter("gen/" + validRegistration.getMsiSdn() + ".txt");
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))
        ) {
            List<String> csvLine = new ArrayList<>();
            csvLine.add(validRegistration.getMsiSdn());
            csvLine.add(validRegistration.getSimType().toString());
            csvLine.add(validRegistration.getName());
            csvLine.add(validRegistration.getDateOfBirth().toString());
            csvLine.add(validRegistration.getGender().toString());
            csvLine.add(validRegistration.getAddress());
            csvLine.add(validRegistration.getIdNumber());
            printer.printRecord(csvLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
