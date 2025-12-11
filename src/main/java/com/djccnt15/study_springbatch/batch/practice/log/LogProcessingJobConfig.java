package com.djccnt15.study_springbatch.batch.practice.log;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.practice.log.model.LogEntry;
import com.djccnt15.study_springbatch.batch.practice.log.model.ProcessedLogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Batch
@RequiredArgsConstructor
public class LogProcessingJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job logProcessingJob(
        Step createDirectoryStep,
        Step logCollectionStep,
        Step logProcessingStep
    ) {
        return new JobBuilder("logProcessingJob", jobRepository)
            .start(createDirectoryStep)
            .next(logCollectionStep)
            .next(logProcessingStep)
            .build();
    }
    
    @Bean
    public Step createDirectoryStep(SystemCommandTasklet mkdirTasklet) {
        return new StepBuilder("createDirectoryStep", jobRepository)
            .tasklet(mkdirTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public SystemCommandTasklet mkdirTasklet(
        @Value("#{jobParameters['date']}") String date
    ) {
        var tasklet = new SystemCommandTasklet();
        tasklet.setWorkingDirectory(System.getProperty("user.home"));
        
        var collectedLogsPath = "collected_ecommerce_logs/%s".formatted(date);
        var processedLogsPath = "processed_logs/%s".formatted(date);
        
        tasklet.setCommand("mkdir", "-p", collectedLogsPath, processedLogsPath, " && ls -al");
        tasklet.setTimeout(3000); // 3초 타임아웃
        return tasklet;
    }
    
    @Bean
    public Step logCollectionStep(SystemCommandTasklet scpTasklet) {
        return new StepBuilder("logCollectionStep", jobRepository)
            .tasklet(scpTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public SystemCommandTasklet scpTasklet(
        @Value("#{jobParameters['date']}") String date
    ) {
        var tasklet = new SystemCommandTasklet();
        tasklet.setWorkingDirectory(System.getProperty("user.home"));
        var processedLogsPath = "collected_ecommerce_logs/" + date;
        
        var commandBuilder = new StringJoiner(" && ");
        for (String host : List.of("localhost")) {
            var command = "scp %s:~/ecommerce_logs/%s.log ./%s/%s.log".formatted(
                host, date, processedLogsPath, host);
            commandBuilder.add(command);
        }
        
        tasklet.setCommand("/bin/sh", "-c", commandBuilder.toString());
        tasklet.setTimeout(10000); //10초 타임아웃
        return tasklet;
    }
    
    @Bean
    public Step logProcessingStep(
        MultiResourceItemReader<LogEntry> multiResourceItemReader,
        LogEntryProcessor logEntryProcessor,
        FlatFileItemWriter<ProcessedLogEntry> processedLogEntryJsonWriter
    ) {
        return new StepBuilder("logProcessingStep", jobRepository)
            .<LogEntry, ProcessedLogEntry>chunk(10, transactionManager)
            .reader(multiResourceItemReader)
            .processor(logEntryProcessor)
            .writer(processedLogEntryJsonWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public MultiResourceItemReader<LogEntry> multiResourceItemReader(
        @Value("#{jobParameters['date']}") String date
    ) {
        var resourceItemReader = new MultiResourceItemReader<LogEntry>();
        resourceItemReader.setName("multiResourceItemReader");
        resourceItemReader.setResources(getResources(date));
        resourceItemReader.setDelegate(logFileReader());
        return resourceItemReader;
    }
    
    private Resource[] getResources(String date) {
        try {
            var userHome = System.getProperty("user.home");
            var location = "file:%s/collected_ecommerce_logs/%s/*.log".formatted(userHome, date);
            
            var resolver = new PathMatchingResourcePatternResolver();
            return resolver.getResources(location);
        } catch (IOException e) {
            throw new RuntimeException("Failed to resolve log files", e);
        }
    }
    
    @Bean
    public FlatFileItemReader<LogEntry> logFileReader() {
        return new FlatFileItemReaderBuilder<LogEntry>()
            .name("logFileReader")
            .delimited()
            .delimiter(",")
            .names("dateTime", "level", "message")
            .targetType(LogEntry.class)
            .build();
    }
    
    @Bean
    public LogEntryProcessor logEntryProcessor() {
        return new LogEntryProcessor();
    }
    
    @Bean
    @StepScope
    public FlatFileItemWriter<ProcessedLogEntry> processedLogEntryJsonWriter(
        @Value("#{jobParameters['date']}") String date
    ) {
        var userHome = System.getProperty("user.home");
        var outputPath = Paths.get(userHome, "processed_logs", date, "processed_logs.jsonl").toString();
        
        var objectMapper = new ObjectMapper();
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        
        return new FlatFileItemWriterBuilder<ProcessedLogEntry>()
            .name("processedLogEntryJsonWriter")
            .resource(new FileSystemResource(outputPath))
            .lineAggregator(item -> {
                try {
                    return objectMapper.writeValueAsString(item);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error converting item to JSON", e);
                }
            })
            .build();
    }
}
