package com.djccnt15.study_springbatch.batch.practice.student.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    
    private Long studentId;
    
    private String currentLecture;
    
    private String instructor;
    
    private String persuasionMethod;
}
