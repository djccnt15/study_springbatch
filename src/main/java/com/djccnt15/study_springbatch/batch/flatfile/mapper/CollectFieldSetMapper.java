package com.djccnt15.study_springbatch.batch.flatfile.mapper;

import com.djccnt15.study_springbatch.batch.flatfile.model.CollectLogLine;
import com.djccnt15.study_springbatch.batch.flatfile.model.SystemLogLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CollectFieldSetMapper implements FieldSetMapper<SystemLogLine> {
    
    @Override
    public SystemLogLine mapFieldSet(FieldSet fieldSet) throws BindException {
        return CollectLogLine.builder()
            .type(fieldSet.readString("type"))
            .dumpType(fieldSet.readString("dumpType"))
            .processId(fieldSet.readString("processId"))
            .timestamp(fieldSet.readString("timestamp"))
            .dumpPath(fieldSet.readString("dumpPath"))
            .build();
    }
}
