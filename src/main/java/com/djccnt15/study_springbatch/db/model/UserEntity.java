package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "USER_MEMBER")
@NoArgsConstructor
@SuperBuilder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Integer age;
    
    private String region;
    
    private String phoneNumber;
}
