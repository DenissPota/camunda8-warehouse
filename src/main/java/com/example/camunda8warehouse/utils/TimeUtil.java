package com.example.camunda8warehouse.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public final class TimeUtil {

    public static final long MILLIS_IN_HOUR = Duration.ofHours(1).toMillis();
    public static final long MILLIS_IN_MINUTE = Duration.ofMinutes(1).toMillis();
    public static final long MILLIS_IN_SECOND = Duration.ofSeconds(1).toMillis();

    public static String getDuration(long startTime) {
        if (startTime <= 0) {
            return startTime + "ms";
        }

        return formatDuration(System.currentTimeMillis() - startTime);
    }

    public static String formatDuration(long duration) {
        if (duration >= MILLIS_IN_HOUR) {
            return duration / MILLIS_IN_HOUR + "h " + formatDuration(duration % MILLIS_IN_HOUR);
        } else if (duration >= MILLIS_IN_MINUTE) {
            return duration / MILLIS_IN_MINUTE + "m " + formatDuration(duration % MILLIS_IN_MINUTE);
        } else if (duration >= MILLIS_IN_SECOND) {
            return duration / MILLIS_IN_SECOND + "s " + formatDuration(duration % MILLIS_IN_SECOND);
        } else {
            return duration + "ms";
        }
    }
}
