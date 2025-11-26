package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flow.decider.StudentReviewDecider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Random;

import static com.djccnt15.study_springbatch.batch.flow.FlowStringConst.*;

@Slf4j
@Batch
@RequiredArgsConstructor
public class StudentReviewJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job studentReviewJob(
        Step analyzeStudentReviewStep,
        StudentReviewDecider studentReviewDecider,
        Step promoteCourseStep,
        Step normalManagementStep,
        Step improvementRequiredStep,
        Step springBatchMasterStep
    ) {
        return new JobBuilder("studentReviewJob", jobRepository)
            .incrementer(new RunIdIncrementer())  // auto adds run.id
            .start(analyzeStudentReviewStep)
            .next(studentReviewDecider) // 이 놈을 주목하라
            .on(EXCELLENT_COURSE).to(promoteCourseStep)
            .from(studentReviewDecider).on(AVERAGE_COURSE).to(normalManagementStep)
            .from(studentReviewDecider).on(NEEDS_IMPROVEMENT).to(improvementRequiredStep)
            .from(studentReviewDecider).on(SPRING_BATCH).to(springBatchMasterStep)
            .end()
            .build();
    }
    
    @Bean
    public Step analyzeStudentReviewStep(Tasklet analyzeStudentReviewTasklet) {
        return new StepBuilder("analyzeStudentReviewStep", jobRepository)
            .tasklet(analyzeStudentReviewTasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet analyzeStudentReviewTasklet() {
        return (contribution, chunkContext) -> {
            // 수강생 리뷰 점수 분석 (샘플을 위해 랜덤 값 사용)
            var random = new Random();
            var reviewScore = random.nextInt(12);  // 0-11 사이 랜덤 점수
            
            log.info("수강생 리뷰 분석 중... 평균 점수: {}/10", reviewScore);
            
            // StepExecution의 ExecutionContext에 분석 결과 저장
            var stepExecution = contribution.getStepExecution();
            var executionContext = stepExecution.getExecutionContext();
            executionContext.putInt("reviewScore", reviewScore);
            
            log.info("수강생 리뷰 분석 완료.");
            return RepeatStatus.FINISHED;
        };
    }
    
    @Bean
    public Step promoteCourseStep() {
        return new StepBuilder("promoteCourseStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("우수 강의 홍보 처리 중...");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step normalManagementStep() {
        return new StepBuilder("normalManagementStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("일반 관리 대상 강의 처리 중...");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step improvementRequiredStep() {
        return new StepBuilder("improvementRequiredStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.warn("개선 필요 강의 처리 중...");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step springBatchMasterStep() {
        return new StepBuilder("springBatchMasterStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.error("스프링 배치 마스터 강의 감지!");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
