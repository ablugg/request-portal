package com.github.ablugg.requestportal.request;

import java.time.Instant;

/**
 * What anyone holding a tracking code may see. The description and contact email are
 * deliberately absent: a tracking code can be forwarded or shoulder-surfed, so the
 * public view carries only what is needed to answer "where is my request?".
 */
public record PublicRequestView(
        String reference,
        RequestCategory category,
        String title,
        RequestStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static PublicRequestView from(ServiceRequest request) {
        return new PublicRequestView(
                request.getReference(),
                request.getCategory(),
                request.getTitle(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}
