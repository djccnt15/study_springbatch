package com.djccnt15.study_springbatch.batch.practice.log;

import com.djccnt15.study_springbatch.batch.practice.log.model.LogEntry;
import com.djccnt15.study_springbatch.batch.practice.log.model.ProcessedLogEntry;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LogEntryProcessor implements ItemProcessor<LogEntry, ProcessedLogEntry> {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("ERROR_CODE\\[(\\w+)]");
    
    @Override
    public ProcessedLogEntry process(LogEntry item) {
        ProcessedLogEntry processedEntry = new ProcessedLogEntry();
        processedEntry.setDateTime(parseDateTime(item.getDateTime()));
        processedEntry.setLevel(parseLevel(item.getLevel()));
        processedEntry.setMessage(item.getMessage());
        processedEntry.setErrorCode(extractErrorCode(item.getMessage()));
        return processedEntry;
    }
    
    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, ISO_FORMATTER);
    }
    
    private LogLevelEnum parseLevel(String level) {
        return LogLevelEnum.fromString(level);
    }
    
    private String extractErrorCode(String message) {
        if (message == null) {
            return null;
        }
        
        var matcher = ERROR_CODE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // ERROR 문자열이 포함되어 있지만 패턴이 일치하지 않는 경우
        if (message.contains("ERROR")) {
            return "UNKNOWN_ERROR";
        }
        return null;
    }
}
