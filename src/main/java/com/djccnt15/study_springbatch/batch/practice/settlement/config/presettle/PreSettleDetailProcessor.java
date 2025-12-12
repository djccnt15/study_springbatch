package com.djccnt15.study_springbatch.batch.practice.settlement.config.presettle;

import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.Key;
import com.djccnt15.study_springbatch.batch.practice.settlement.enums.ServicePolicy;
import com.djccnt15.study_springbatch.batch.practice.settlement.enums.State;
import com.djccnt15.study_springbatch.db.model.ApiOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PreSettleDetailProcessor implements ItemProcessor<ApiOrderEntity, Key> {
    
    @Override
    public Key process(ApiOrderEntity item) throws Exception {
        if (item.getState().equals(State.FAIL)) {
            return null;
        }
        
        var serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();
        
        return new Key(item.getCustomerId(), serviceId);
    }
}
