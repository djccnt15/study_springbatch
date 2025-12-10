package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "USER_NEW")
@NoArgsConstructor
@SuperBuilder
public class UserNewEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String name;
    
    @Column
    private Integer age;
    
    @Column
    private String region;
    
    @Column
    private String phoneNumber;
}
