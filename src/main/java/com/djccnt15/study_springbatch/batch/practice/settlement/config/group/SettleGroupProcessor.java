package com.djccnt15.study_springbatch.batch.practice.settlement.config.group;

import com.djccnt15.study_springbatch.batch.practice.settlement.model.Customer;
import com.djccnt15.study_springbatch.db.model.SettleGroupEntity;
import com.djccnt15.study_springbatch.db.repository.SettleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettleGroupProcessor implements ItemProcessor<Customer, List<SettleGroupEntity>>, StepExecutionListener {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final SettleGroupRepository settleGroupRepository;
    private StepExecution stepExecution;
    
    @Override
    public List<SettleGroupEntity> process(Customer item) throws Exception {
        var targetDate = stepExecution.getJobParameters().getString("targetDate");
        var end = LocalDate.parse(targetDate, formatter);
        return settleGroupRepository.findGroupByCustomerIdAndServiceId(
            end.minusDays(6),
            end,
            item.getId()
        );
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
