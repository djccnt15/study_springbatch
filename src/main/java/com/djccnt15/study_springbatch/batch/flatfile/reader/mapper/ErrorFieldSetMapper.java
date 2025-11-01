package com.djccnt15.study_springbatch.batch.flatfile.reader.mapper;

import com.djccnt15.study_springbatch.batch.flatfile.reader.model.ErrorLogLine;
import com.djccnt15.study_springbatch.batch.flatfile.reader.model.SystemLogLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ErrorFieldSetMapper implements FieldSetMapper<SystemLogLine> {
    
    @Override
    public SystemLogLine mapFieldSet(FieldSet fieldSet) throws BindException {
        return ErrorLogLine.builder()
            .type(fieldSet.readString("type"))
            .application(fieldSet.readString("application"))
            .errorType(fieldSet.readString("errorType"))
            .timestamp(fieldSet.readString("timestamp"))
            .message(fieldSet.readString("message"))
            .resourceUsage(fieldSet.readString("resourceUsage"))
            .logPath(fieldSet.readString("logPath"))
            .build();
    }
}
