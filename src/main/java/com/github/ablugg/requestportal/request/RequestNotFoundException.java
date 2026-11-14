package com.github.ablugg.requestportal.request;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(String reference) {
        super("No request found with reference " + reference);
    }
}
