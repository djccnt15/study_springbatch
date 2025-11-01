package com.djccnt15.study_springbatch.batch.flatfile.writer.model;

public record FlatFileItemWriteRecord(
    String id,
    String name,
    String date,
    String cause
) {}
