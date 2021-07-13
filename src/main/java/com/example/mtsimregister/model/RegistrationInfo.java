package com.example.mtsimregister.model;

import com.example.mtsimregister.constant.Gender;
import com.example.mtsimregister.constant.SimType;
import com.example.mtsimregister.constant.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@Valid
@NoArgsConstructor
public class RegistrationInfo {
    @NotBlank(message = "MSISDN is Empty!")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$", message = "MSISDN does not comply to country's standard")
    private String msiSdn;
    @NotNull(message = "SIM Type is Invalid! Should be POSTPAID or PREPAID only")
    private SimType simType;
    @NotBlank(message = "Name is Empty!")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Name contains Special Character!")
    private String name;
    @NotNull(message = "Date of Birth is empty or not valid")
    @Past(message = "Current Date or Future Date can't be used!")
    private LocalDate dateOfBirth;
    @NotNull(message = "Gender is Invalid! Should be M or F only")
    private Gender gender;
    @NotBlank(message = "Address is empty!")
    @Size(min = 20, message = "Address is shorter than 20 characters!")
    private String address;
    @NotBlank(message = "Id number is Empty!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$", message = "id number must be combination of alphabet and numbers")
    private String idNumber;
    private Status status;
}
