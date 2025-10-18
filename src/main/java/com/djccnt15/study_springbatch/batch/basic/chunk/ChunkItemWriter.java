package com.djccnt15.study_springbatch.batch.basic.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

// 청크 단위로 데이터를 저장
@Slf4j
public class ChunkItemWriter implements ItemWriter<String> {
    
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        log.info("chunk {}", chunk);
    }
}
