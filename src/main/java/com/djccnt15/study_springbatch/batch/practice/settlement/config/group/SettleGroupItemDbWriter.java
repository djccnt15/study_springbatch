package com.djccnt15.study_springbatch.batch.practice.settlement.config.group;

import com.djccnt15.study_springbatch.db.repository.SettleGroupRepository;
import com.djccnt15.study_springbatch.db.model.SettleGroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettleGroupItemDbWriter implements ItemWriter<List<SettleGroupEntity>> {
    
    private final SettleGroupRepository settleGroupRepository;
    
    @Override
    public void write(Chunk<? extends List<SettleGroupEntity>> chunk) throws Exception {
        var settleGroups = new ArrayList<SettleGroupEntity>();
        
        chunk.forEach(settleGroups::addAll);
        
        settleGroupRepository.saveAll(settleGroups);
    }
}
