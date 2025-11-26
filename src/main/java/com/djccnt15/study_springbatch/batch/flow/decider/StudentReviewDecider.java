package com.djccnt15.study_springbatch.batch.flow.decider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import static com.djccnt15.study_springbatch.batch.flow.FlowStringConst.*;

@Slf4j
@Component
public class StudentReviewDecider implements JobExecutionDecider {
    
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // StepExecution의 ExecutionContext에서 분석 결과 추출
        var executionContext = stepExecution.getExecutionContext();
        var reviewScore = executionContext.getInt("reviewScore");
        
        log.info("수강생 리뷰 점수 기반 강의 분류 중", reviewScore);
        
        // 리뷰 점수에 따른 강의 분류
        if (reviewScore > 10) {
            log.error("스프링 배치 마스터 감지!!!");
            return new FlowExecutionStatus(SPRING_BATCH);
        } else if (reviewScore >= 8) {
            log.info("우수 강의 감지! 홍보 대상으로 분류");
            return new FlowExecutionStatus(EXCELLENT_COURSE);
        } else if (reviewScore >= 5) {
            log.info("평균 강의 감지. 일반 관리 대상으로 분류");
            return new FlowExecutionStatus(AVERAGE_COURSE);
        } else {
            log.warn("저평가 강의 감지! 개선 필요 대상으로 분류");
            return new FlowExecutionStatus(NEEDS_IMPROVEMENT);
        }
    }
}
