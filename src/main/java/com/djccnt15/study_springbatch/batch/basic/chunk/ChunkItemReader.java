package com.djccnt15.study_springbatch.batch.basic.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

// 데이터 소스에서 데이터를 하나씩 반환. `read()` 메서드가 호출될 때마다 데이터를 순차적으로 반환
// 내부적으로 `BufferedReader`를 사용해 일정 크기(16kb)의 데이터를 버퍼에 저장 후 순차적 출력
// 청크 크기 만큼 `read()` 메서드를 호출하여 하나의 청크 생성
@Slf4j
public class ChunkItemReader implements ItemReader<Integer> {
    
    private int count = 0;
    
    @Override
    public Integer read() throws Exception {
        count++;
        log.info("Read {}", count);
        if (count == 15) {
            return null;  // `null` 반환 시 더 이상 읽을 데이터가 없는 것으로 판단 후 청크 단위 반복 종료
        }
        return count;
    }
}
