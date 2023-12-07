package com.batch_java.study_spring_batch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ServicePolicy {
    A(1L, "/url/services/a", 10),
    B(2L, "/url/services/b", 11),
    C(3L, "/url/services/c", 12),
    D(4L, "/url/services/d", 13),
    E(5L, "/url/services/e", 14),
    F(6L, "/url/services/f", 15),
    G(7L, "/url/services/g", 16),
    H(8L, "/url/services/h", 17),
    I(9L, "/url/services/i", 18),
    J(10L, "/url/services/j", 19),
    K(11L, "/url/services/k", 20),
    L(12L, "/url/services/l", 21),
    M(13L, "/url/services/m", 22),
    N(14L, "/url/services/n", 23),
    O(15L, "/url/services/o", 24),
    P(16L, "/url/services/p", 25),
    Q(17L, "/url/services/q", 26),
    R(18L, "/url/services/r", 27),
    S(19L, "/url/services/s", 28),
    T(20L, "/url/services/t", 29),
    U(21L, "/url/services/u", 30),
    V(22L, "/url/services/v", 31),
    W(23L, "/url/services/w", 32),
    X(24L, "/url/services/x", 33),
    Y(25L, "/url/services/y", 34),
    Z(26L, "/url/services/z", 35),
    ;
    
    private final Long id;
    private final String url;
    private final Integer fee;
    
    public static ServicePolicy findByUrl(String url) {
        return Arrays.stream(values())
            .filter(it -> it.url.equals(url))
            .findFirst()
            .orElseThrow();
    }
    
    public static ServicePolicy findById(Long id) {
        return Arrays.stream(values())
            .filter(it -> it.id.equals(id))
            .findFirst()
            .orElseThrow();
    }
}
