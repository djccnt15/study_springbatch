package com.djccnt15.study_springbatch.batch.itemprocessor;

import com.djccnt15.study_springbatch.batch.itemprocessor.model.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ExecutionerProcessor implements ItemProcessor<Command, Command> {
    
    @Override
    public Command process(Command command) {
        // 시스템 파괴 명령어 실행자는 처단
        if (command.getCommandText().contains("rm -rf /") ||
            command.getCommandText().contains("kill -9")) {
            log.info("{}의 {} -> 시스템 파괴자 처단 완료. 기록에서 말살",
                command.getUserId(),
                command.getCommandText());
            return null;
        }
        
        // sudo 권한 남용자는 처단
        if (command.isSudoUsed() &&
            command.getTargetProcess().contains("system")) {
            log.info("{}의 sudo {} -> 권한 남용자 처단 완료. 기록에서 抹殺",
                command.getUserId(),
                command.getCommandText());
            return null;
        }
        
        log.info("{}의 {} -> 시스템 준수자 생존. 최종 기록 허가",
            command.getUserId(),
            command.getCommandText());
        return command;
    }
}
