package com.djccnt15.study_springbatch.batch.jobparameter;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.jobparameter.model.PojoJobParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class PojoJobParamBatchConfig {
    
    @Bean
    public Job pojoJobParamJob(JobRepository jobRepository, Step pojoJobParamStep) {
        return new JobBuilder("pojoJobParamJob", jobRepository)
            .start(pojoJobParamStep)
            .build();
    }
    
    @Bean
    public Step pojoJobParamStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet pojoJobParamTasklet
    ) {
        return new StepBuilder("pojoJobParamStep", jobRepository)
            .tasklet(pojoJobParamTasklet, transactionManager)
            .build();
    }
    
    @Bean
    public Tasklet pojoJobParamTasklet(PojoJobParam pojoJobParam) {
        return (contribution, chunkContext) -> {
            log.info("field inject: {}", pojoJobParam.getFieldInject());
            log.info("constructor inject: {}", pojoJobParam.getConstructorInject());
            log.info("setter inject: {}", pojoJobParam.getSetterInject());
            return RepeatStatus.FINISHED;
        };
    }
}
