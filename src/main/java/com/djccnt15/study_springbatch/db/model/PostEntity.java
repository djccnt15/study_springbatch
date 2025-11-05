package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class PostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "content")
    private String content;
    
    @Column(name = "writer")
    private String writer;
    
    @Builder.Default
    @OneToMany(mappedBy = "post")
    @ToString.Exclude
    private List<ReportEntity> reports = new ArrayList<>();
}
