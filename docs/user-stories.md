# User stories: stage 1

Each acceptance criterion is covered by an automated test in
`src/test/java/com/github/ablugg/requestportal/request/RequestApiTest.java`.
The test name is in brackets.

## Story 1: Submit a request

**As a** customer, **I want to** submit a service request online **so that** I don't have to phone or email to get help.

Acceptance criteria:

1. Given a category, title, description and valid email, when I submit, then the request is saved with status SUBMITTED and I receive a tracking code. [`submittingAValidRequestReturns201WithATrackingCode`]
2. Given a blank title or an invalid email, when I submit, then nothing is saved and I get a specific message for each field that needs fixing. [`submittingAnInvalidRequestReturns400WithAMessagePerField`]
3. Given a category that does not exist, when I submit, then the request is rejected. [`submittingAnUnknownCategoryReturns400`]

## Story 2: Track a request

**As a** customer, **I want to** check my request with my tracking code **so that** I know where it stands without contacting staff.

Acceptance criteria:

1. Given a valid tracking code, when I look it up, then I see the title, category, status and dates. [`trackingShowsStatusButNeverContactDetails`]
2. The tracking view never shows the description or contact email, because a tracking code can be shared or overseen. [`trackingShowsStatusButNeverContactDetails`]
3. Given a code that does not exist, when I look it up, then I am told no request was found. [`trackingAnUnknownCodeReturns404`]
4. Tracking codes are random, so one customer cannot guess another's. [`RequestStatusTest.referencesHaveTheExpectedShape`]

## Story 3: See the queue

**As a** staff member, **I want to** list requests and filter them by status **so that** I can see what needs attention.

Acceptance criteria:

1. When I list requests, then I see the full record for each, newest first, including the contact email. [`staffCanListRequestsFilteredByStatus`]
2. When I filter by a status, then I see only requests in that status. [`staffCanListRequestsFilteredByStatus`]

## Story 4: Move a request through its lifecycle

**As a** staff member, **I want to** update a request's status **so that** the customer sees progress.

Allowed moves:

| From | To |
|---|---|
| SUBMITTED | IN_PROGRESS, CLOSED |
| IN_PROGRESS | RESOLVED, CLOSED |
| RESOLVED | CLOSED, IN_PROGRESS (reopened) |
| CLOSED | none (final) |

Acceptance criteria:

1. Given an allowed move, when I update the status, then the customer's tracking view shows the new status. [`aStatusChangeByStaffIsVisibleToTheCustomer`]
2. Given a move that is not allowed, when I update the status, then it is rejected with an explanation and nothing changes. [`aClosedRequestCannotBeReopened`]
3. Given a tracking code that does not exist, when I update the status, then I am told no request was found. [`changingTheStatusOfAnUnknownRequestReturns404`]

## Not in stage 1

- Staff login. The staff endpoints are open until stage 2.
- A user interface. Stage 3 adds the React front end.
- Email notifications.
