package com.github.ablugg.requestportal.request;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * End-to-end API tests. @SpringBootTest starts the whole application (controllers,
 * service, repository, in-memory database) and MockMvc sends it HTTP requests without
 * opening a real network port. Each test maps to an acceptance criterion in
 * docs/user-stories.md.
 */
@SpringBootTest
class RequestApiTest {

    private static final String VALID_BODY = """
            {
              "category": "TECHNICAL_ISSUE",
              "title": "Cannot reset my password",
              "description": "The reset email never arrives.",
              "contactEmail": "sam@example.com"
            }
            """;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // --- Story 1: submit a request -------------------------------------------------

    @Test
    void submittingAValidRequestReturns201WithATrackingCode() throws Exception {
        mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference", matchesPattern("SR-[A-HJKMNP-Z2-9]{8}")))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.title").value("Cannot reset my password"))
                .andExpect(header().string("Location", matchesPattern("/api/requests/SR-.{8}")));
    }

    @Test
    void submittingAnInvalidRequestReturns400WithAMessagePerField() throws Exception {
        String body = """
                {
                  "category": "BILLING",
                  "title": "   ",
                  "description": "Charged twice.",
                  "contactEmail": "not-an-email"
                }
                """;

        mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Enter a short title"))
                .andExpect(jsonPath("$.errors.contactEmail").value("Enter a valid email address"));
    }

    @Test
    void submittingAnUnknownCategoryReturns400() throws Exception {
        String body = VALID_BODY.replace("TECHNICAL_ISSUE", "NOT_A_CATEGORY");

        mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // --- Story 2: track a request --------------------------------------------------

    @Test
    void trackingShowsStatusButNeverContactDetails() throws Exception {
        String reference = submitAndGetReference();

        mvc.perform(get("/api/requests/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value(reference))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.contactEmail").doesNotExist())
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    @Test
    void trackingAnUnknownCodeReturns404() throws Exception {
        mvc.perform(get("/api/requests/{reference}", "SR-ZZZZZZZZ"))
                .andExpect(status().isNotFound());
    }

    // --- Story 3: staff see the queue ----------------------------------------------

    @Test
    void staffCanListRequestsFilteredByStatus() throws Exception {
        String reference = submitAndGetReference();

        mvc.perform(get("/api/staff/requests").param("status", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].reference", hasItem(reference)))
                .andExpect(jsonPath("$[0].contactEmail").exists());

        mvc.perform(get("/api/staff/requests").param("status", "CLOSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].reference", not(hasItem(reference))));
    }

    // --- Story 4: staff move a request through its lifecycle -----------------------

    @Test
    void aStatusChangeByStaffIsVisibleToTheCustomer() throws Exception {
        String reference = submitAndGetReference();

        changeStatus(reference, "IN_PROGRESS")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mvc.perform(get("/api/requests/{reference}", reference))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void aClosedRequestCannotBeReopened() throws Exception {
        String reference = submitAndGetReference();
        changeStatus(reference, "CLOSED").andExpect(status().isOk());

        changeStatus(reference, "IN_PROGRESS")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A request that is CLOSED cannot be moved to IN_PROGRESS"));
    }

    @Test
    void changingTheStatusOfAnUnknownRequestReturns404() throws Exception {
        changeStatus("SR-ZZZZZZZZ", "IN_PROGRESS").andExpect(status().isNotFound());
    }

    // --- helpers --------------------------------------------------------------------

    private String submitAndGetReference() throws Exception {
        String response = mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.reference");
    }

    private org.springframework.test.web.servlet.ResultActions changeStatus(String reference, String status)
            throws Exception {
        return mvc.perform(patch("/api/staff/requests/{reference}/status", reference)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"" + status + "\"}"));
    }
}
