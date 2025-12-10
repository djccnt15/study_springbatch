package com.djccnt15.study_springbatch.batch.practice.student;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.practice.student.model.Student;
import com.djccnt15.study_springbatch.batch.practice.student.model.Target;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class StudentJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Bean
    public Job studentJob() {
        return new JobBuilder("studentJob", jobRepository)
            .start(studentStep())
            .build();
    }
    
    @Bean
    public Step studentStep() {
        return new StepBuilder("studentStep", jobRepository)
            .<Student, Target>chunk(10, transactionManager)
            .reader(studentsReader())
            .processor(studentProcessor())
            .writer(targetWriter(null))
            .build();
    }
    
    @Bean
    public JdbcPagingItemReader<Student> studentsReader() {
        return new JdbcPagingItemReaderBuilder<Student>()
            .name("studentsReader")
            .dataSource(dataSource)
            .selectClause("SELECT id, current_lecture, instructor, persuasion_method")
            .fromClause("FROM student")
            .sortKeys(Map.of("id", Order.ASCENDING))
            .beanRowMapper(Student.class)
            .pageSize(10)
            .build();
    }
    
    @Bean
    public StudentProcessor studentProcessor() {
        return new StudentProcessor();
    }
    
    @Bean
    @StepScope
    public FlatFileItemWriter<Target> targetWriter(
        @Value("#{jobParameters['filePath']}") String filePath
    ) {
        return new FlatFileItemWriterBuilder<Target>()
            .name("targetWriter")
            .resource(new FileSystemResource(filePath + "/target.jsonl"))
            .lineAggregator(item -> {
                try {
                    return objectMapper.writeValueAsString(item);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error converting brainwashed victim to JSON", e);
                }
            })
            .build();
    }
}
