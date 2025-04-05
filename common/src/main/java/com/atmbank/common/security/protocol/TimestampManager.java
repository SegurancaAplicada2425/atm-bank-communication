package com.atmbank.common.security.protocol;

import com.atmbank.common.config.Constants;

public class TimestampManager {
    private static final long TIMESTAMP_VALIDITY = Constants.TIMESTAMP_VALIDITY;

    public boolean isValidTimestamp(long timestamp) {
        long currentTimestamp = getCurrentTimestamp();
        return (currentTimestamp - timestamp) <= TIMESTAMP_VALIDITY;
    }

    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
