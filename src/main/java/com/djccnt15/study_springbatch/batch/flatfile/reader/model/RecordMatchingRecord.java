package com.djccnt15.study_springbatch.batch.flatfile.reader.model;

public record RecordMatchingRecord(
    String command,
    int cpu,
    String status
) {}
