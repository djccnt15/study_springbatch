package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import static com.djccnt15.study_springbatch.batch.flow.FlowStringConst.*;

@Slf4j
@Batch
@RequiredArgsConstructor
public class LectureReviewJobBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    private final Tasklet analyzeLectureTasklet;
    
    @Bean
    public Job lectureReviewJob() {
        return new JobBuilder("lectureReviewJob", jobRepository)
            .incrementer(new RunIdIncrementer())  // auto adds run.id
            // .on() 을 사용할 경우, 반드시 해당 단계에서 발생 가능한 모든 조건(ExitStatus)에 대한 분기 처리 설정 필요
            .start(analyzeLectureStep())
            .on(APPROVED).to(approvedStep())
            // .on("*").end("COMPLETED_BY_SYSTEM")  // 커스텀 상태의 EndState로 전이
            
            .from(analyzeLectureStep())
            .on(PLAGIARISM_DETECTED).to(plagiarismDetectedStep())
            // .on(PLAGIARISM_DETECTED).fail()  // 표절 감지 즉시 실패 처리, 추가 단계 없음
            
            .from(analyzeLectureStep())
            .on(QUALITY_SUBSTANDARD).to(lowQualityRejectionStep())
            
            .from(analyzeLectureStep())
            .on(TOO_EXPENSIVE).to(priceGougerDetectedStep())
            .on("*").end()  // priceGougerDetectedStep 결과와 상관없이 성공 종료
            
            .from(analyzeLectureStep())
            .on(UNKNOWN_PANIC).to(adminManualCheckStep())
            .on("*").stop()  // adminManualCheckStep의 결과와 상관없이 STOPPED 상태의 EndState로 전이
            
            .end().build();
    }
    
    @Bean
    public Step analyzeLectureStep() {
        return new StepBuilder("analyzeLectureStep", jobRepository)
            .tasklet(analyzeLectureTasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
    
    @Bean
    public Step approvedStep() {
        return new StepBuilder("approvedStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("approved business logic..");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step plagiarismDetectedStep() {
        return new StepBuilder("plagiarismDetectedStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.warn("plagiarism detected business logic..");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step lowQualityRejectionStep() {
        return new StepBuilder("lowQualityRejectionStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.warn("low quality business logic..");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step priceGougerDetectedStep() {
        return new StepBuilder("priceGougerDetectedStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.warn("price gouger business logic..");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step adminManualCheckStep() {
        return new StepBuilder("adminManualCheckStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.error("admin business logic..");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
