package com.djccnt15.study_springbatch.batch.exitcode;

import java.util.Map;

public final class ExitCodeMap {
    
    public static final String ERROR_CASE_1 = "IllegalStateException";
    public static final String ERROR_CASE_2 = "ValidationException";
    public static final String ERROR_CASE_3 = "IllegalArgumentException";
    
    private static final int EXITCODE_SKULL_FRACTURE = 3;
    private static final int EXITCODE_SYSTEM_BRUTALIZED = 4;
    private static final int EXITCODE_UNKNOWN_CHAOS = 5;
    
    public static final Map<String, Integer> ExitCodeMap = Map.of(
        ERROR_CASE_1, EXITCODE_SKULL_FRACTURE,
        ERROR_CASE_2, EXITCODE_SYSTEM_BRUTALIZED,
        ERROR_CASE_3, EXITCODE_UNKNOWN_CHAOS
    );
}
