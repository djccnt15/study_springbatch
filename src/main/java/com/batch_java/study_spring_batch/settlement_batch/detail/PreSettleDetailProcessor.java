package com.batch_java.study_spring_batch.settlement_batch.detail;

import com.batch_java.study_spring_batch.domain.ServicePolicy;
import com.batch_java.study_spring_batch.enums.State;
import com.batch_java.study_spring_batch.model.ApiOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PreSettleDetailProcessor implements ItemProcessor<ApiOrder, Key> {
    
    @Override
    public Key process(ApiOrder item) throws Exception {
        if (item.getState().equals(State.FAIL)) {
            return null;
        }
        
        final Long serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();
        
        return new Key(item.getCustomerId(), serviceId);
    }
}
