package com.djccnt15.study_springbatch.batch.jobexecution;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionParamValidator implements JobParametersValidator {
    
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (parameters == null) {
            throw new JobParametersInvalidException("파라미터가 NULL입니다");
        }
        
        var jobParamLevel = parameters.getLong("jobParam.level");
        if (jobParamLevel == null) {
            throw new JobParametersInvalidException("jobParam.level 파라미터는 필수값임");
        }
        
        if (jobParamLevel > 9) {
            throw new JobParametersInvalidException(
                "jobParam.level 허용치 초과: %s (최대 허용치: 9)".formatted(jobParamLevel));
        }
    }
}
