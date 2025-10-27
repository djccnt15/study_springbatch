package com.djccnt15.study_springbatch.batch.flatfile.model;

public record RecordMatchingRecord(
    String command,
    int cpu,
    String status
) {}
