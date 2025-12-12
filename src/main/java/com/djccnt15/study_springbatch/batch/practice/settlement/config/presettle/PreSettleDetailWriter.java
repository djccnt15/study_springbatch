package com.djccnt15.study_springbatch.batch.practice.settlement.config.presettle;

import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class PreSettleDetailWriter implements ItemWriter<Key>, StepExecutionListener {
    
    private StepExecution stepExecution;
    
    @Override
    public void write(Chunk<? extends Key> chunk) throws Exception {
        var snapshotMap = (ConcurrentMap<Key, Long>) stepExecution.getExecutionContext().get("snapshots");
        chunk.forEach(key -> snapshotMap.compute(key, (k, v) -> (v == null) ? 1 : v + 1));
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        log.info("stepExecution: {}", stepExecution);
        
        var snapshotMap = new ConcurrentHashMap<Key, Long>();
        stepExecution.getExecutionContext().put("snapshots", snapshotMap);
    }
}
