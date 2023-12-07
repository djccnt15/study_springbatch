package com.batch_java.study_spring_batch.settlement_batch.detail;

import java.io.Serializable;

record Key(Long customerId, Long serviceId) implements Serializable {
}
