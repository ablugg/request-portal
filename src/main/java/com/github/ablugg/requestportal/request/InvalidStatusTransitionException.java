package com.github.ablugg.requestportal.request;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(RequestStatus from, RequestStatus to) {
        super("A request that is " + from + " cannot be moved to " + to);
    }
}
