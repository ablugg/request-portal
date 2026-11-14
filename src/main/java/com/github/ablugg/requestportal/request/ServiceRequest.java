package com.github.ablugg.requestportal.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * One row in the service_requests table. JPA (Hibernate) reads these annotations to
 * create the table and to turn rows into objects and back.
 */
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    /** Internal database key. Never shown to the public; they use {@link #reference}. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 16)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RequestCategory category;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 254)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    /** JPA needs a no-argument constructor; application code uses the one below. */
    protected ServiceRequest() {
    }

    public ServiceRequest(String reference, RequestCategory category, String title,
                          String description, String contactEmail, Instant now) {
        this.reference = reference;
        this.category = category;
        this.title = title;
        this.description = description;
        this.contactEmail = contactEmail;
        this.status = RequestStatus.SUBMITTED;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** The only way to change status, so an illegal move can never be saved. */
    public void changeStatus(RequestStatus next, Instant now) {
        if (!status.canMoveTo(next)) {
            throw new InvalidStatusTransitionException(status, next);
        }
        this.status = next;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public RequestCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
