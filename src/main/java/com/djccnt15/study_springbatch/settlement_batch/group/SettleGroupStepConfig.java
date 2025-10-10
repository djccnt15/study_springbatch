package com.djccnt15.study_springbatch.settlement_batch.group;

import com.djccnt15.study_springbatch.common.Batch;
import com.djccnt15.study_springbatch.model.SettleGroupEntity;
import com.djccnt15.study_springbatch.settlement_batch.group.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Batch
@RequiredArgsConstructor
public class SettleGroupStepConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    
    @Bean
    public Step settleGroupStep(
        SettleGroupReader settleGroupReader,
        SettleGroupProcessor settleGroupProcessor,
        ItemWriter<List<SettleGroupEntity>> settleGroupItemWriter
    ) {
        return new StepBuilder("settleGroupStep", jobRepository)
            .<Customer, List<SettleGroupEntity>>chunk(100, platformTransactionManager)
            .reader(settleGroupReader)
            .processor(settleGroupProcessor)
            .writer(settleGroupItemWriter)
            .build();
    }
    
    @Bean
    public ItemWriter<List<SettleGroupEntity>> settleGroupItemWriter(
        SettleGroupItemDbWriter settleGroupItemDbWriter,
        SettleGroupItemMailWriter settleGroupItemMailWriter
    ) {
        return new CompositeItemWriter<>(
            settleGroupItemDbWriter,
            settleGroupItemMailWriter
        );
    }
}
