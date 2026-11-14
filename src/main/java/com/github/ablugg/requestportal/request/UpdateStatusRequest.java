package com.github.ablugg.requestportal.request;

import jakarta.validation.constraints.NotNull;

/** The JSON body staff send to move a request to a new status. */
public record UpdateStatusRequest(

        @NotNull(message = "Choose a status")
        RequestStatus status) {
}
