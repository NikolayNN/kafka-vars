package by.aurorasoft.kafka.model.retry;

public interface Retryable {

    /**
     * Checks if the maximum number of retry attempts has been reached.
     *
     * @return {@code true} if the number of attempts made has reached or exceeded
     * the maximum allowed attempts, indicating that no further retry attempts should be made;
     * {@code false} if there are still retry attempts left.
     */
    boolean isMaxAttemptsReached();

    /**
     * Checks if the timeout since the last attempt has passed, allowing for a new attempt.
     *
     * @return {@code true} if enough time has passed since the last attempt based on the specified timeout;
     * {@code false} otherwise.
     */
    boolean hasTimeoutPassed();

    /**
     * Evaluates the current retry state and determines the appropriate retry status.
     *
     * @return the current {@link Retry.Status}, indicating whether it's ready to retry,
     * waiting for the timeout period to elapse, or if the maximum number of attempts has been reached.
     */
    Retry.Status evaluateRetryStatus();
}
