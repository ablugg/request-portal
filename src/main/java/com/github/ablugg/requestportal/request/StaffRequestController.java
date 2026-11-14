package com.github.ablugg.requestportal.request;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Back-office endpoints: see the queue and move requests through their lifecycle.
 * Protected by Spring Security: requires STAFF role via HTTP Basic authentication.
 */
@RestController
@RequestMapping("/api/staff/requests")
public class StaffRequestController {

    private final ServiceRequestService service;

    public StaffRequestController(ServiceRequestService service) {
        this.service = service;
    }

    /** GET /api/staff/requests, optionally filtered: /api/staff/requests?status=SUBMITTED */
    @GetMapping
    public List<StaffRequestView> list(
            @RequestParam(name = "status", required = false) RequestStatus status) {
        return service.list(status).stream().map(StaffRequestView::from).toList();
    }

    /** PATCH /api/staff/requests/SR-7K3M9Q2A/status with {"status":"IN_PROGRESS"} */
    @PatchMapping("/{reference}/status")
    public StaffRequestView changeStatus(@PathVariable("reference") String reference,
                                         @Valid @RequestBody UpdateStatusRequest body) {
        return StaffRequestView.from(service.changeStatus(reference, body.status()));
    }
}
