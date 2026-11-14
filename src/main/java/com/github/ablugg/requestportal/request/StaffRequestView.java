package com.github.ablugg.requestportal.request;

import java.time.Instant;

/** The full record, for staff working the request. */
public record StaffRequestView(
        String reference,
        RequestCategory category,
        String title,
        String description,
        String contactEmail,
        RequestStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static StaffRequestView from(ServiceRequest request) {
        return new StaffRequestView(
                request.getReference(),
                request.getCategory(),
                request.getTitle(),
                request.getDescription(),
                request.getContactEmail(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}
