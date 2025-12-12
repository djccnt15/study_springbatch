package com.djccnt15.study_springbatch.batch.practice.settlement.config.model;

import java.io.Serializable;

public record Key(
    Long customerId,
    Long serviceId
) implements Serializable {
}
