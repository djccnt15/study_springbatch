package com.djccnt15.study_springbatch.batch.rdb.jdbc.writer;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.rdb.jdbc.writer.model.HackedOrder;
import com.djccnt15.study_springbatch.db.model.enums.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class OrderRecoveryJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    
    @Bean
    public Job orderRecoveryJob() {
        return new JobBuilder("orderRecoveryJob", jobRepository)
            .start(orderRecoveryStep())
            .build();
    }
    
    @Bean
    public Step orderRecoveryStep() {
        return new StepBuilder("orderRecoveryStep", jobRepository)
            .<HackedOrder, HackedOrder>chunk(10, transactionManager)
            .reader(compromisedOrderReader())
            .processor(orderStatusProcessor())
            .writer(orderStatusWriter())
            .build();
    }
    
    @Bean
    public JdbcPagingItemReader<HackedOrder> compromisedOrderReader() {
        return new JdbcPagingItemReaderBuilder<HackedOrder>()
            .name("compromisedOrderReader")
            .dataSource(dataSource)
            .pageSize(10)
            .selectClause("SELECT id, customer_id, order_datetime, status, shipping_id")
            .fromClause("FROM orders")
            .whereClause("""
                WHERE
                    (status = 'SHIPPED' AND shipping_id IS NULL)
                    OR (status = 'CANCELLED' AND shipping_id IS NOT NULL)
                """)
            .sortKeys(Map.of("id", Order.ASCENDING))
            .beanRowMapper(HackedOrder.class)
            .build();
    }
    
    @Bean
    public ItemProcessor<HackedOrder, HackedOrder> orderStatusProcessor() {
        return order -> {
            if (order.getShippingId() == null) {
                order.setStatus(OrderStatusEnum.READY_FOR_SHIPMENT.toString());
            } else {
                order.setStatus(OrderStatusEnum.SHIPPED.toString());
            }
            return order;
        };
    }
    
    @Bean
    public JdbcBatchItemWriter<HackedOrder> orderStatusWriter() {
        return new JdbcBatchItemWriterBuilder<HackedOrder>()
            .dataSource(dataSource)
            .sql("UPDATE orders SET status = :status WHERE id = :id")
            .beanMapped()
            // true (기본값): 단 하나의 데이터라도 업데이트/추가 실패하면 즉시 예외를 던져 배치 중단
            // 모든 데이터가 반드시 처리되어야 하는 경우에 사용
            // false: 일부 데이터가 업데이트되지 않아도 배치를 계속 진행
            // 중복 데이터 처리 구문을 사용할 때나 조건부 UPDATE처럼 일부 데이터는 변경되지 않아도 되는 상황에서 사용
            // (INSERT IGNORE 또는 INSERT ... ON CONFLICT DO NOTHING)
            .assertUpdates(true)
            .build();
    }
}
