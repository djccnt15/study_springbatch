package com.djccnt15.study_springbatch.batch.practice.settlement.config.detail;

import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.Key;
import com.djccnt15.study_springbatch.batch.practice.settlement.config.model.KeyAndCount;
import com.djccnt15.study_springbatch.batch.practice.settlement.config.presettle.PreSettleDetailProcessor;
import com.djccnt15.study_springbatch.batch.practice.settlement.config.presettle.PreSettleDetailWriter;
import com.djccnt15.study_springbatch.db.model.ApiOrderEntity;
import com.djccnt15.study_springbatch.db.model.SettleDetailEntity;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SettleDetailStepConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    
    @Bean
    public Step preSettleDetailStep(
        JpaCursorItemReader<ApiOrderEntity> preSettleDetailReader,
        PreSettleDetailWriter preSettleDetailWriter,
        ExecutionContextPromotionListener exPromotionListener
    ) {
        return new StepBuilder("preSettleDetailStep", jobRepository)
            .<ApiOrderEntity, Key>chunk(5000, platformTransactionManager)
            .reader(preSettleDetailReader)
            .processor(new PreSettleDetailProcessor())
            .writer(preSettleDetailWriter)
            .listener(exPromotionListener)
            .build();
    }
    
    @Bean
    @StepScope
    public JpaCursorItemReader<ApiOrderEntity> preSettleDetailReader(
        @Value("#{jobParameters['targetDate']}") String targetDate
    ) {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        var period_end = LocalDate.parse(targetDate, formatter);
        var period_start = period_end.minusDays(1);
        
        return new JpaCursorItemReaderBuilder<ApiOrderEntity>()
            .name("preSettleDetailReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("""
                SELECT a
                FROM ApiOrderEntity a
                WHERE 1=1
                    AND a.requestedAt BETWEEN '%s' and '%s'
                ORDER BY a.requestedAt
                """.formatted(period_start.toString(), period_end.toString()))
            .build();
    }
    
    @Bean
    public Step settleDetailStep(
        SettleDetailReader settleDetailReader,
        SettleDetailProcessor settleDetailProcessor,
        JpaItemWriter<SettleDetailEntity> settleDetailJpaItemWriter
    ) {
        return new StepBuilder("settleDetailStep", jobRepository)
            .<KeyAndCount, SettleDetailEntity>chunk(1000, platformTransactionManager)
            .reader(settleDetailReader)
            .processor(settleDetailProcessor)
            .writer(settleDetailJpaItemWriter)
            .build();
    }
    
    @Bean
    public ExecutionContextPromotionListener exPromotionListener() {
        var listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"snapshots"});
        return listener;
    }
    
    @Bean
    public JpaItemWriter<SettleDetailEntity> settleDetailJpaItemWriter() {
        return new JpaItemWriterBuilder<SettleDetailEntity>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
