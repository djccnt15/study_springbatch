package com.batch_sample.batchjava.customer.entity;

import com.batch_sample.batchjava.customer.enums.CustomerStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class CustomerEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    private String email;
    
    private LocalDateTime createAt;
    
    @Setter
    private LocalDateTime loginAt;
    
    @Setter
    private CustomerStatus status;
    
    public CustomerEntity(String name, String email) {
        this.name = name;
        this.email = email;
        this.createAt = LocalDateTime.now();
        this.loginAt = LocalDateTime.now();
        this.status = CustomerStatus.NORMAL;
    }
}
