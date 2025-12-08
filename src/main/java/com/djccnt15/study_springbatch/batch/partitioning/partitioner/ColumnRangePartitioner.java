package com.djccnt15.study_springbatch.batch.partitioning.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ColumnRangePartitioner implements Partitioner {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ColumnRangePartitioner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        var min = jdbcTemplate.queryForObject("SELECT MIN(id) from USER", Integer.class);
        var max = jdbcTemplate.queryForObject("SELECT MAX(id) from USER", Integer.class);
        var targetSize = (max - min) / gridSize + 1;
        
        var result = new HashMap<String, ExecutionContext>();
        var number = 0;
        int start = min;
        var end = start + targetSize - 1;
        
        while (start <= max) {
            var value = new ExecutionContext();
            result.put("partition" + number, value);
            
            if (end >= max) {
                end = max;
            }
            
            value.putInt("minValue", start);
            value.putInt("maxValue", end);
            
            start += targetSize;
            end += targetSize;
            number ++;
        }
        
        return result;
    }
}
