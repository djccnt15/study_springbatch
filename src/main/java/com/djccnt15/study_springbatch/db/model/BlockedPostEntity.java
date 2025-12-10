package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blocked_posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedPostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blocked_posts_id_seq")
    @SequenceGenerator(
        name = "blocked_posts_id_seq",
        sequenceName = "blocked_posts_id_seq",
        allocationSize = 100
    )
    private Long id;
    
    @Column(name = "post_id")
    private Long postId;
    
    @Column
    private String writer;
    
    @Column
    private String title;
    
    @Column(name = "report_count")
    private int reportCount;
    
    @Column(name = "block_score")
    private double blockScore;
    
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
}
