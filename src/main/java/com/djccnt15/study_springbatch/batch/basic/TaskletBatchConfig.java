package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.batch.basic.tasklet.BasicTasklet;
import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;


// `Tasklet` 지향 처리. 비교적 복잡하지 않은 단순한 작업을 실행할 때 사용
@Slf4j
@Batch
@RequiredArgsConstructor
public class TaskletBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job taskletOrientedJob() {
        return new JobBuilder("taskletOrientedJob", jobRepository)
            .start(taskletOrientedStep())  // Step 등록
            .build();
    }
    
    @Bean
    public Step taskletOrientedStep() {
        return new StepBuilder("taskletOrientedStep", jobRepository)
            .tasklet(tasklet(), transactionManager)  // Tasklet과 transactionManager 설정
            .build();
    }
    
    @Bean
    public Tasklet tasklet() {
        return new BasicTasklet();
    }
}
