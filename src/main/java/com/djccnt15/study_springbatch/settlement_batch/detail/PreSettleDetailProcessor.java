package com.djccnt15.study_springbatch.settlement_batch.detail;

import com.djccnt15.study_springbatch.domain.ServicePolicy;
import com.djccnt15.study_springbatch.enums.State;
import com.djccnt15.study_springbatch.model.ApiOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PreSettleDetailProcessor implements ItemProcessor<ApiOrderEntity, Key> {
    
    @Override
    public Key process(ApiOrderEntity item) throws Exception {
        if (item.getState().equals(State.FAIL)) {
            return null;
        }
        
        final Long serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();
        
        return new Key(item.getCustomerId(), serviceId);
    }
}
