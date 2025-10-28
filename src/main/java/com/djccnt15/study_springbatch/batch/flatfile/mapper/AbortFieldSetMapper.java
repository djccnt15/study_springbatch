package com.djccnt15.study_springbatch.batch.flatfile.mapper;

import com.djccnt15.study_springbatch.batch.flatfile.model.AbortLogLine;
import com.djccnt15.study_springbatch.batch.flatfile.model.SystemLogLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class AbortFieldSetMapper implements FieldSetMapper<SystemLogLine> {
    
    @Override
    public SystemLogLine mapFieldSet(FieldSet fieldSet) throws BindException {
        return AbortLogLine.builder()
            .type(fieldSet.readString("type"))
            .application(fieldSet.readString("application"))
            .errorType(fieldSet.readString("errorType"))
            .timestamp(fieldSet.readString("timestamp"))
            .message(fieldSet.readString("message"))
            .exitCode(fieldSet.readString("exitCode"))
            .processPath(fieldSet.readString("processPath"))
            .status(fieldSet.readString("status"))
            .build();
    }
}
