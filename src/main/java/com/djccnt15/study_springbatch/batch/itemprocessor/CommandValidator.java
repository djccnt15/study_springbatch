package com.djccnt15.study_springbatch.batch.itemprocessor;

import com.djccnt15.study_springbatch.batch.itemprocessor.model.Command;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class CommandValidator implements Validator<Command> {
    
    @Override
    public void validate(Command command) throws ValidationException {
        if (command.getCommandText().contains("rm -rf /") ||
            command.getCommandText().contains("kill -9")) {
            var message = "%s의 %s → 시스템 파괴 명령어 감지. 처리"
                .formatted(command.getUserId(), command.getCommandText());
            throw new ValidationException(message);
        }
        
        if (command.isSudoUsed() &&
            command.getTargetProcess().contains("system")) {
            var message = "%s의 sudo %s → 권한 남용 감지. 처리"
                .formatted(command.getUserId(), command.getCommandText());
            throw new ValidationException(message);
        }
    }
}
