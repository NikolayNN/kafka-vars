package by.aurorasoft.kafka.model.retry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ExceptionInfo {
    String message;
    String exceptionClass;
    String rootCauseMessage;
    String rootCauseClass;

    public ExceptionInfo(Throwable e) {
        this.message = e.getMessage();
        this.exceptionClass = e.getClass().getName();
        Throwable rootCause = findRootCause(e);
        this.rootCauseMessage = rootCause.getMessage();
        this.rootCauseClass = rootCause.getClass().getName();
    }

    @JsonCreator
    public ExceptionInfo(@JsonProperty("message") String message,
                         @JsonProperty("exceptionClass") String exceptionClass,
                         @JsonProperty("rootCauseMessage") String rootCauseMessage,
                         @JsonProperty("rootCauseClass") String rootCauseClass) {
        this.message = message;
        this.exceptionClass = exceptionClass;
        this.rootCauseMessage = rootCauseMessage;
        this.rootCauseClass = rootCauseClass;
    }

    private Throwable findRootCause(Throwable e) {
        Throwable cause = e.getCause();
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause == null ? e : cause;
    }
}
