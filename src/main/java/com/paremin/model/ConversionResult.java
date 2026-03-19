package com.paremin.model;

/**
 * Holds the outcome of a single conversion job.
 * Can represent success or failure.
 */
public class ConversionResult {

    public enum Status { SUCCESS, FAILED }

    private final ConversionTask task;
    private final Status status;
    private final long durationMs;
    private final String errorMessage;

    private ConversionResult(ConversionTask task, Status status,
                             long durationMs, String errorMessage) {
        this.task = task;
        this.status = status;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
    }

    public static ConversionResult success(ConversionTask task, long durationMs) {
        return new ConversionResult(task, Status.SUCCESS, durationMs, null);
    }

    public static ConversionResult failure(ConversionTask task, String errorMessage, long durationMs) {
        return new ConversionResult(task, Status.FAILED, durationMs, errorMessage);
    }

    public ConversionTask getTask() { return task; }
    public Status getStatus() { return status; }
    public long getDurationMs() { return durationMs; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isSuccess() { return status == Status.SUCCESS; }

    @Override
    public String toString() {
        if (isSuccess()) {
            return String.format("SUCCESS [%s] converted in %dms",
                    task.getInputPath().getFileName(), durationMs);
        } else {
            return String.format("FAILED  [%s] %s",
                    task.getInputPath().getFileName(), errorMessage);
        }
    }
}