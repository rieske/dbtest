package io.github.rieske.dbtest.extension;

import java.time.Duration;

final class TimeUtils {
    private TimeUtils() {
    }

    static Duration durationSince(long startTime) {
        return Duration.ofMillis(System.currentTimeMillis() - startTime);
    }
}
