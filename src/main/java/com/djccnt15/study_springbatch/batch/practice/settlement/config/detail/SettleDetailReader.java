package com.djccnt15.study_springbatch.batch.practice.settlement.config.detail;

import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.Key;
import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.KeyAndCount;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SettleDetailReader implements ItemReader<KeyAndCount>, StepExecutionListener {
    
    private Iterator<Map.Entry<Key, Long>> iterator;
    
    @Override
    public KeyAndCount read() throws Exception {
        if (!iterator.hasNext())
            return null;
        var map = iterator.next();
        return new KeyAndCount(map.getKey(), map.getValue());
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        var jobExecution = stepExecution.getJobExecution();
        var snapshots = (ConcurrentHashMap<Key, Long>) jobExecution.getExecutionContext().get("snapshots");
        iterator = snapshots.entrySet().iterator();
    }
}
