package by.aurorasoft.kafka.model.retry;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
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

    private Throwable findRootCause(Throwable e) {
        Throwable cause = e.getCause();
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause == null ? e : cause;
    }
}
