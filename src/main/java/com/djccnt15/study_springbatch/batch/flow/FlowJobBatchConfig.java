package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;

@Slf4j
@Batch
@RequiredArgsConstructor
public class FlowJobBatchConfig {
    
    private final JobRepository jobRepository;
    
    @Bean
    public Job flowJobBatchJob(
        Flow lectureValidationFlow,
        Step notifyInstructorStep
    ) {
        return new JobBuilder("flowJobBatchJob", jobRepository)
            .start(lectureValidationFlow)  // Flow를 Job의 시작점으로 사용
            .next(notifyInstructorStep)    // Flow 완료 후 추가 Step 실행
            .end().build();
    }
}
