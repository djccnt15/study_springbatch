package com.djccnt15.study_springbatch.batch.itemprocessor;

import com.djccnt15.study_springbatch.batch.itemprocessor.model.CommandReport;
import com.djccnt15.study_springbatch.batch.itemprocessor.model.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CommandAnalyzer implements ItemProcessor<SystemLog, CommandReport> {
    
    @Override
    public CommandReport process(SystemLog systemLog) {
        CommandReport report = new CommandReport();
        report.setExecutorId(systemLog.getUserId());
        report.setTimestamp(systemLog.getExecutedAt());
        
        // 명령어 분석 및 위험도 평가 💀
        if (systemLog.getRawCommand().contains("rm -rf")) {
            report.setAction("시스템 파일 제거 시도");
            report.setSeverity("CRITICAL");
        } else if (systemLog.getRawCommand().contains("kill -9")) {
            report.setAction("프로세스 강제 종료 시도");
            report.setSeverity("HIGH");
        } else {
            report.setAction(analyzeCommand(systemLog.getRawCommand()));
            report.setSeverity("LOW");
        }
        
        log.info("⚔️ {}의 행적 분석 완료: {}",
            systemLog.getUserId(),
            report.getAction());
        return report;
    }
    
    private String analyzeCommand(String command) {
        // 일반 명령어 분석 로직 💀
        return "일반 시스템 명령어 실행";
    }
}
