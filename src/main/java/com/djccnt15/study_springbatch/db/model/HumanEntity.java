package com.djccnt15.study_springbatch.db.model;

import com.djccnt15.study_springbatch.db.model.enums.HumanRankEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Data
@Entity
@Table(name = "humans")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String name;
    
    @Column
    @Enumerated(EnumType.STRING)
    private HumanRankEnum humanRank;
    
    @Column
    private Boolean executed;
    
    @OneToMany(mappedBy = "humanEntity", fetch = FetchType.EAGER)
    @BatchSize(size = 100)
    private List<ActivityEntity> activities;
}
