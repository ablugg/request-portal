package com.github.ablugg.requestportal.request;

/**
 * Lifecycle of a service request. The allowed moves live here, next to the states,
 * so there is exactly one place that answers "can a request go from X to Y?".
 *
 * <pre>
 * SUBMITTED   -> IN_PROGRESS, CLOSED
 * IN_PROGRESS -> RESOLVED, CLOSED
 * RESOLVED    -> CLOSED, IN_PROGRESS (reopened)
 * CLOSED      -> (final)
 * </pre>
 */
public enum RequestStatus {
    SUBMITTED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public boolean canMoveTo(RequestStatus next) {
        return switch (this) {
            case SUBMITTED -> next == IN_PROGRESS || next == CLOSED;
            case IN_PROGRESS -> next == RESOLVED || next == CLOSED;
            case RESOLVED -> next == CLOSED || next == IN_PROGRESS;
            case CLOSED -> false;
        };
    }
}
