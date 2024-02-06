package by.aurorasoft.kafka.model.retry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * The {@code Meta} class represents metadata for tracking retry attempts.
 * It includes information such as the number of attempts made, the time of the last attempt,
 * the maximum number of attempts allowed, and the time of the first attempt.
 * This class is typically used in scenarios where an operation needs to be retried
 * multiple times, such as in network communications or remote service invocations.
 */
public class Retry<T> {
    Meta retryMeta;
    ExceptionInfo exceptionInfo;
    T obj;

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Meta {
        private int attemptCount;
        private Instant lastAttemptTime;
        private final int maxAttempts;
        private final Instant firstAttemptTime;

        public Meta(int maxAttempts) {
            this.attemptCount = 0;
            this.maxAttempts = maxAttempts;
            this.lastAttemptTime = Instant.now();
            this.firstAttemptTime = Instant.now();
        }

        /**
         * Records a new attempt, incrementing the attempt count and updating the last attempt time.
         */
        public void recordAttempt() {
            this.attemptCount++;
            this.lastAttemptTime = Instant.now();
        }

        /**
         * Determines if another retry attempt can be made.
         *
         * @return {@code true} if the current attempt count is less than the maximum attempts;
         *         {@code false} otherwise.
         */
        public boolean canRetry() {
            return attemptCount < maxAttempts;
        }
    }
}

