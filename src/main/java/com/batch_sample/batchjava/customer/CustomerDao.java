package com.batch_sample.batchjava.customer;

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
public class CustomerDao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    private String email;
    
    private LocalDateTime createAt;
    
    @Setter
    private LocalDateTime loginAt;
    
    @Setter
    private StatusEnum status;
    
    public CustomerDao(String name, String email) {
        this.name = name;
        this.email = email;
        this.createAt = LocalDateTime.now();
        this.loginAt = LocalDateTime.now();
        this.status = StatusEnum.NORMAL;
    }
}
