package com.github.ablugg.requestportal.request;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic. Controllers handle HTTP, the repository handles SQL, and the rules
 * about what the application actually does sit here in between.
 */
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    /** Spring passes the repository in (constructor injection); nothing here calls "new". */
    public ServiceRequestService(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ServiceRequest submit(CreateRequest input) {
        ServiceRequest request = new ServiceRequest(
                newUniqueReference(),
                input.category(),
                input.title().trim(),
                input.description().trim(),
                input.contactEmail().trim(),
                Instant.now());
        return repository.save(request);
    }

    @Transactional(readOnly = true)
    public ServiceRequest getByReference(String reference) {
        return repository.findByReference(reference)
                .orElseThrow(() -> new RequestNotFoundException(reference));
    }

    @Transactional(readOnly = true)
    public List<ServiceRequest> list(RequestStatus status) {
        return status == null
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional
    public ServiceRequest changeStatus(String reference, RequestStatus next) {
        ServiceRequest request = getByReference(reference);
        request.changeStatus(next, Instant.now());
        // No save() call needed: inside a transaction, Hibernate notices the changed
        // field and writes the UPDATE when the method returns.
        return request;
    }

    private String newUniqueReference() {
        String reference = ReferenceGenerator.next();
        while (repository.existsByReference(reference)) {
            reference = ReferenceGenerator.next();
        }
        return reference;
    }
}
