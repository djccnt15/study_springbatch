package com.djccnt15.study_springbatch.settlement_batch.detail;

import java.io.Serializable;

record Key(Long customerId, Long serviceId) implements Serializable {
}
