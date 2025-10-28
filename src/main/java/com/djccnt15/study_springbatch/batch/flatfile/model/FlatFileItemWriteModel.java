package com.djccnt15.study_springbatch.batch.flatfile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FlatFileItemWriteModel {
    
    private String id;
    
    private String name;
    
    private String date;
    
    private String cause;
}
