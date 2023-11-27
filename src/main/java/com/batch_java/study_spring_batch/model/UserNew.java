package com.batch_java.study_spring_batch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "USER_NEW")
@NoArgsConstructor
@SuperBuilder
public class UserNew {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Integer age;
    
    private String region;
    
    private String phoneNumber;
}
