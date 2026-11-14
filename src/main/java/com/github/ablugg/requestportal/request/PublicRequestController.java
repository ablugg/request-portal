package com.github.ablugg.requestportal.request;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Customer-facing endpoints: submit a request, then track it with the code you got back. */
@RestController
@RequestMapping("/api/requests")
public class PublicRequestController {

    private final ServiceRequestService service;

    public PublicRequestController(ServiceRequestService service) {
        this.service = service;
    }

    /** POST /api/requests -> 201 Created, with the new request and a Location header. */
    @PostMapping
    public ResponseEntity<PublicRequestView> submit(@Valid @RequestBody CreateRequest body) {
        ServiceRequest saved = service.submit(body);
        URI location = URI.create("/api/requests/" + saved.getReference());
        return ResponseEntity.created(location).body(PublicRequestView.from(saved));
    }

    /** GET /api/requests/SR-7K3M9Q2A -> 200 with the public view, or 404. */
    @GetMapping("/{reference}")
    public PublicRequestView track(@PathVariable("reference") String reference) {
        return PublicRequestView.from(service.getByReference(reference));
    }
}
