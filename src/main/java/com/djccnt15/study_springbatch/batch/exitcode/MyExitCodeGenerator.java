package com.djccnt15.study_springbatch.batch.exitcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyExitCodeGenerator implements JobExecutionListener, ExitCodeGenerator {
    
    private final SimpleJvmExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
    
    private int exitCode = 0;
    
    public MyExitCodeGenerator() {
        exitCodeMapper.setMapping(ExitCodeMap.ExitCodeMap);
    }
    
    @Override
    public int getExitCode() {
        return exitCode;
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        String exitStatus = jobExecution.getExitStatus().getExitCode();
        
        this.exitCode = exitCodeMapper.intValue(exitStatus);
        log.info("Exit Status: {}", exitStatus);
        log.info("System Exit Code: {}", exitCode);
    }
}
