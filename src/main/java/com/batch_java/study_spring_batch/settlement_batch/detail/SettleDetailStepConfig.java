package com.batch_java.study_spring_batch.settlement_batch.detail;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.model.ApiOrder;
import com.batch_java.study_spring_batch.model.SettleDetail;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
@RequiredArgsConstructor
public class SettleDetailStepConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    
    @Bean
    public Step preSettleDetailStep(
        JpaCursorItemReader<ApiOrder> preSettleDetailReader,
        PreSettleDetailWriter preSettleDetailWriter,
        ExecutionContextPromotionListener executionContextPromotionListener
    ) {
        return new StepBuilder("preSettleDetailStep", jobRepository)
            .<ApiOrder, Key>chunk(5000, platformTransactionManager)
            .reader(preSettleDetailReader)
            .processor(new PreSettleDetailProcessor())
            .writer(preSettleDetailWriter)
            .listener(executionContextPromotionListener)
            .build();
    }
    
    @Bean
    @StepScope
    public JpaCursorItemReader<ApiOrder> preSettleDetailReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaCursorItemReaderBuilder<ApiOrder>()
            .name("preSettleDetailReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT a FROM ApiOrder a ORDER BY a.requestedAt")
            .build();
    }
    
    @Bean
    public Step settleDetailStep(
        SettleDetailReader settleDetailReader,
        SettleDetailProcessor settleDetailProcessor,
        JpaItemWriter<SettleDetail> settleDetailJpaItemWriter
    ) {
        return new StepBuilder("settleDetailStep", jobRepository)
            .<KeyAndCount, SettleDetail>chunk(1000, platformTransactionManager)
            .reader(settleDetailReader)
            .processor(settleDetailProcessor)
            .writer(settleDetailJpaItemWriter)
            .build();
    }
    
    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"snapshots"});
        return listener;
    }
    
    @Bean
    public JpaItemWriter<SettleDetail> settleDetailJpaItemWriter(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaItemWriterBuilder<SettleDetail>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
