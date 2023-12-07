package com.batch_java.study_spring_batch.settlement_batch.detail;

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
        final ConcurrentMap<Key, Long> snapshotMap = (ConcurrentMap<Key, Long>) stepExecution.getExecutionContext().get("snapshots");
        chunk.forEach(key -> snapshotMap.compute(key, (k, v) -> (v == null) ? 1 : v + 1));
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        log.info("stepExecution: {}", stepExecution);
        
        final ConcurrentMap<Key, Long> snapshotMap = new ConcurrentHashMap<>();
        stepExecution.getExecutionContext().put("snapshots", snapshotMap);
    }
}
