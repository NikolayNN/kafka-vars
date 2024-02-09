package by.aurorasoft.kafka.model.retry;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;


@Getter
public class Retry<T> implements Retryable {
    private final T obj;
    private final Meta retryMeta;
    private ExceptionInfo exceptionInfo;

    public Retry(int maxAttempts, Duration attemptTimeout, T obj) {
        this.retryMeta = new Meta(maxAttempts, attemptTimeout);
        this.obj = obj;
    }

    @JsonCreator
    public Retry(T obj, Meta retryMeta, ExceptionInfo exceptionInfo) {
        this.obj = obj;
        this.retryMeta = retryMeta;
        this.exceptionInfo = exceptionInfo;
    }

    /**
     * Records a new attempt, incrementing the attempt count and updating the last attempt time.
     */
    public void recordAttempt(Throwable e) {
        retryMeta.recordAttempt();
        exceptionInfo = new ExceptionInfo(e);
    }

    @Override
    public boolean isMaxAttemptsReached() {
        return retryMeta.isMaxAttemptsReached();
    }

    @Override
    public boolean hasTimeoutPassed() {
        return retryMeta.hasTimeoutPassed();
    }

    @Override
    public Status evaluateRetryStatus() {
        return retryMeta.evaluateRetryStatus();
    }


    /**
     * Represents the metadata associated with retry attempts for a specific operation.
     * This class tracks the number of attempts made, the timestamp of the last attempt,
     * and determines whether additional attempts can be made based on the configured
     * maximum number of attempts and the timeout duration between attempts.
     * <p>
     * The {@code attemptTimeout} ensures that retries are not made too frequently,
     * allowing for a cooldown period between attempts. The {@code maxAttempts} limits
     * the total number of retries to prevent infinite retry loops.
     * </p>
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Meta implements Retryable {

        /**
         * The current number of retry attempts that have been made.
         */
        private int attemptCount;

        /**
         * The timestamp of the last retry attempt.
         */
        private Instant lastAttemptTime;

        /**
         * The maximum number of retry attempts allowed.
         */
        private final int maxAttempts;

        /**
         * The duration to wait before making another retry attempt.
         */
        private final Duration attemptTimeout;

        /**
         * The timestamp of the first retry attempt.
         */
        private final Instant firstAttemptTime;

        public Meta(int maxAttempts, Duration attemptTimeout) {
            this.attemptCount = 0;
            this.attemptTimeout = attemptTimeout;
            this.maxAttempts = maxAttempts;
            this.lastAttemptTime = Instant.now();
            this.firstAttemptTime = Instant.now();
        }

        @JsonCreator
        public Meta(int attemptCount, Instant lastAttemptTime, int maxAttempts, Duration attemptTimeout, Instant firstAttemptTime) {
            this.attemptCount = attemptCount;
            this.lastAttemptTime = lastAttemptTime;
            this.maxAttempts = maxAttempts;
            this.attemptTimeout = attemptTimeout;
            this.firstAttemptTime = firstAttemptTime;
        }

        /**
         * Records a new attempt, incrementing the attempt count and updating the last attempt time.
         */
        public void recordAttempt() {
            this.attemptCount++;
            this.lastAttemptTime = Instant.now();
        }

        /**
         * @see Retryable#isMaxAttemptsReached();
         */
        @Override
        public boolean isMaxAttemptsReached() {
            return attemptCount >= maxAttempts;
        }

        /**
         * @see Retryable#hasTimeoutPassed();
         */
        @Override
        public boolean hasTimeoutPassed() {
            Duration timeSinceLastAttempt = Duration.between(lastAttemptTime, Instant.now());
            return timeSinceLastAttempt.compareTo(attemptTimeout) >= 0;
        }

        /**
         * @see Retryable#evaluateRetryStatus();
         */
        @Override
        public Status evaluateRetryStatus() {
            if (isMaxAttemptsReached()) {
                return Status.MAX_ATTEMPTS_REACHED;
            }
            if (!hasTimeoutPassed()) {
                return Status.WAITING_FOR_TIMEOUT;
            }
            return Status.READY_TO_RETRY;
        }
    }

    public enum Status {
        READY_TO_RETRY,
        WAITING_FOR_TIMEOUT,
        MAX_ATTEMPTS_REACHED
    }
}

