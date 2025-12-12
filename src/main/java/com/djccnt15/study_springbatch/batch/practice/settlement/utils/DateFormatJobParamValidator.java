package com.djccnt15.study_springbatch.batch.practice.settlement.utils;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateFormatJobParamValidator implements JobParametersValidator {
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final String[] names;
    
    public DateFormatJobParamValidator(String[] names) {
        this.names = names;
    }
    
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        for (String name: names) {
            validateDateFormat(parameters, name);
        }
    }
    
    private void validateDateFormat(JobParameters parameters, String name) throws JobParametersInvalidException {
        try {
            var string = parameters.getString(name);
            LocalDate.parse(Objects.requireNonNull(string), dateTimeFormatter);
        } catch (Exception e) {
            throw new JobParametersInvalidException("supports only yyyyMMdd style");
        }
    }
}
