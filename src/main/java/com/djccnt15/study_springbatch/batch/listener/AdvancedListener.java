package com.djccnt15.study_springbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.djccnt15.study_springbatch.batch.listener.ListenerString.*;

@Slf4j
@Component
public class AdvancedListener implements JobExecutionListener {
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        var jobExecutePlan = generateJobExecutePlan();
        jobExecution.getExecutionContext().put(JOB_EXECUTE_PLAN, jobExecutePlan);
        log.info("beforeJob - TARGET_SYSTEM: {}",  jobExecutePlan.get(TARGET_SYSTEM));
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        var executionContext = jobExecution.getExecutionContext();
        var jobExecutePlan = (Map<String, Object>) executionContext.get(JOB_EXECUTE_PLAN);
        var jobResult = (String) executionContext.get(JOB_RESULT);
        
        log.info("afterJob - TARGET_SYSTEM: {} JOB_RESULT: {}", jobExecutePlan.get(TARGET_SYSTEM), jobResult);
        
        if (TERMINATED.equals(jobResult)) {
            log.info(TERMINATED);
        } else {
            log.info(DETECTED);
        }
    }
    
    private Map<String, Object> generateJobExecutePlan() {
        var targets = List.of(
            "판교 서버실", "안산 데이터센터"
        );
        var objectives = List.of(
            "kill -9 실행", "rm -rf 전개", "chmod 000 적용", "/dev/null로 리다이렉션"
        );
        var targetData = List.of(
            "코어 덤프 파일", "시스템 로그", "설정 파일", "백업 데이터"
        );
        var requiredTools = List.of(
            "USB 킬러", "널 바이트 인젝터", "커널 패닉 유발기", "메모리 시퍼너"
        );
        
        var rand = new Random();
        
        var jobExecutePlan = new HashMap<String, Object>();
        jobExecutePlan.put(TARGET_SYSTEM, targets.get(rand.nextInt(targets.size())));
        jobExecutePlan.put(OBJECTIVE, objectives.get(rand.nextInt(objectives.size())));
        jobExecutePlan.put(TARGET_DATA, targetData.get(rand.nextInt(targetData.size())));
        jobExecutePlan.put(REQUIRED_TOOLS, requiredTools.get(rand.nextInt(requiredTools.size())));
        
        return jobExecutePlan;
    }
}
