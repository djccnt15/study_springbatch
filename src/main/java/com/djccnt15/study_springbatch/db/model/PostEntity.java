package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String title;
    
    @Column
    private String content;
    
    @Column
    private String writer;
    
    @Builder.Default
    @OneToMany(mappedBy = "post")
    @ToString.Exclude
    private List<ReportEntity> reports = new ArrayList<>();
}
