package com.djccnt15.study_springbatch.batch.flatfile.json.model;

public record SystemFailureRecord(
    String command,
    int cpu,
    String status
) {}
