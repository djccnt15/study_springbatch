package com.djccnt15.study_springbatch.batch.basic.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

// 청크의 아이템을 1개씩 처리
// 청크의 아이템마다 `process()`를 반복 호출해서 데이터를 변환하거나 필터링
// 청크 크기 만큼 `process()` 메서드를 호출하여 하나의 청크 처리
@Slf4j
public class ChunkItemProcessor implements ItemProcessor<Integer, String> {
    
    @Override
    public String process(Integer item) throws Exception {
        return item.toString();
    }
}
