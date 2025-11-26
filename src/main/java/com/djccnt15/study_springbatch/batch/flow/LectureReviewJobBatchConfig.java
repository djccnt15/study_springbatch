package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flow.model.Lecture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Batch
@RequiredArgsConstructor
public class LectureReviewJobBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    private final String APPROVED = "APPROVED";
    private final String PLAGIARISM_DETECTED = "PLAGIARISM_DETECTED";
    private final String QUALITY_SUBSTANDARD = "QUALITY_SUBSTANDARD";
    private final String TOO_EXPENSIVE = "TOO_EXPENSIVE";
    private final String UNKNOWN_PANIC = "UNKNOWN_PANIC";
    
    @Bean
    public Job lectureReviewJob() {
        return new JobBuilder("lectureReviewJob", jobRepository)
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
            .tasklet(analyzeLectureTasklet(lectureList()), transactionManager)
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
    
    @Bean
    @StepScope
    public Tasklet analyzeLectureTasklet(List<Lecture> lectureList) {
        return (contribution, chunkContext) -> {
            // 랜덤하게 강의 선택
            var random = new Random();
            var lecture = lectureList.get(random.nextInt(lectureList.size()));
            
            log.info("강의 분석 시작: {}", lecture);
            
            if (lecture.title().contains("죽음의 Spring Batch")) {
                log.error("unknown error detected alarm");
                contribution.setExitStatus(
                    new ExitStatus(UNKNOWN_PANIC,"시스템 불안정성 감지:"));
            }
            else if (lecture.title().toLowerCase().contains("copy") ||
                lecture.content().toLowerCase().contains("출처:")) {
                log.warn("plagiarism detected alarm");
                contribution.setExitStatus(
                    new ExitStatus(PLAGIARISM_DETECTED, "표절 의심 패턴 발견"));
            }
            else if (lecture.price() > 90000 && lecture.title().contains("억대 연봉")) {
                log.warn("price gouger alarm");
                contribution.setExitStatus(
                    new ExitStatus(TOO_EXPENSIVE, "비정상적 고가 강의"));
            }
            else if (lecture.content().contains("ChatGPT는 실수를 할 수 있습니다") ||
                lecture.title().contains("5분 완성")) {
                log.warn("low quality alarm");
                contribution.setExitStatus(
                    new ExitStatus(QUALITY_SUBSTANDARD, "강의 내용 부실"));
            }
            else {
                log.info("approve lecture");
                contribution.setExitStatus(
                    new ExitStatus(APPROVED, "모든 검증 통과"));
            }
            
            return RepeatStatus.FINISHED;
        };
    }
    
    @Bean
    public List<Lecture> lectureList() {
        var lectures = new ArrayList<Lecture>();
        
        // 정상 강의
        lectures.add(new Lecture(
            "스프링 배치 완벽 안내서",
            29900,
            "스프링 배치 완벽"
        ));
        
        // 고가 강의
        lectures.add(new Lecture(
            "1주일만에 억대 연봉 개발자 되기",
            99000,
            "개비싼 쓰레기 강의"
        ));
        
        // 표절 의심 강의
        lectures.add(new Lecture(
            "Copy & Paste로 배우는 자바스크립트",
            39900,
            "... [여기에 내용 붙여넣기 - 출처: fake-it-till-you-make-it.dev/spring-batch-flow/34] ..."
        ));
        
        // 내용 부실 강의
        lectures.add(new Lecture(
            "5분 완성 Spring Batch 핵심 개념",
            13200,
            "... ChatGPT는 실수를 할 수 있습니다. ..."
        ));
        
        // 시스템 패닉 강의 by KILL9
        lectures.add(new Lecture(
            "죽음의 Spring Batch",
            66666,
            "Unknown Error"
        ));
        
        return lectures;
    }
}
