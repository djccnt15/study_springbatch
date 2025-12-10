package com.djccnt15.study_springbatch.batch.practice.student.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Target {
    
    private Long victimId;
    
    private String originalLecture;
    
    private String originalInstructor;
    
    private String brainwashMessage;
    
    private String newMaster;
    
    private String conversionMethod;
    
    private String brainwashStatus;
    
    private String nextAction;
}
