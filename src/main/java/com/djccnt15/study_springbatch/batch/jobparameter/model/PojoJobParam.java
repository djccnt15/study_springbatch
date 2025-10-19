package com.djccnt15.study_springbatch.batch.jobparameter.model;

import lombok.Data;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@StepScope
@Component
public class PojoJobParam {
    
    @Value("#{jobParameters[fieldInject]}")
    private String fieldInject;
    
    private final String constructorInject;
    
    private int setterInject;
    
    public PojoJobParam(
        @Value("#{jobParameters[constructorInject]}") String constructorInject
    ) {
        this.constructorInject = constructorInject;
    }
    
    @Value("#{jobParameters[setterInject]}")
    public void setSetterInject(int setterInject) {
        this.setterInject = setterInject;
    }
}
