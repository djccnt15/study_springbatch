package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.batch.flow.model.Lecture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.djccnt15.study_springbatch.batch.flow.FlowStringConst.*;

@Slf4j
@Configuration
public class FlowBatchConfig {
    
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
}
