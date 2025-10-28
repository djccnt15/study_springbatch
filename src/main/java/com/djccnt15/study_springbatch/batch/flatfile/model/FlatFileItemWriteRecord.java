package com.djccnt15.study_springbatch.batch.flatfile.model;

public record FlatFileItemWriteRecord(
    String victimId,
    String victimName,
    String executionDate,
    String causeOfDeath
) {}
