package com.github.ablugg.requestportal.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Plain unit tests: no Spring, no database, runs in milliseconds. */
class RequestStatusTest {

    @Test
    void submittedCanBePickedUpOrClosed() {
        assertThat(RequestStatus.SUBMITTED.canMoveTo(RequestStatus.IN_PROGRESS)).isTrue();
        assertThat(RequestStatus.SUBMITTED.canMoveTo(RequestStatus.CLOSED)).isTrue();
        assertThat(RequestStatus.SUBMITTED.canMoveTo(RequestStatus.RESOLVED)).isFalse();
    }

    @Test
    void inProgressCanBeResolvedOrClosed() {
        assertThat(RequestStatus.IN_PROGRESS.canMoveTo(RequestStatus.RESOLVED)).isTrue();
        assertThat(RequestStatus.IN_PROGRESS.canMoveTo(RequestStatus.CLOSED)).isTrue();
        assertThat(RequestStatus.IN_PROGRESS.canMoveTo(RequestStatus.SUBMITTED)).isFalse();
    }

    @Test
    void resolvedCanBeClosedOrReopened() {
        assertThat(RequestStatus.RESOLVED.canMoveTo(RequestStatus.CLOSED)).isTrue();
        assertThat(RequestStatus.RESOLVED.canMoveTo(RequestStatus.IN_PROGRESS)).isTrue();
        assertThat(RequestStatus.RESOLVED.canMoveTo(RequestStatus.SUBMITTED)).isFalse();
    }

    @Test
    void closedIsFinal() {
        for (RequestStatus next : RequestStatus.values()) {
            assertThat(RequestStatus.CLOSED.canMoveTo(next)).isFalse();
        }
    }

    @Test
    void aStatusCannotMoveToItself() {
        for (RequestStatus status : RequestStatus.values()) {
            assertThat(status.canMoveTo(status)).isFalse();
        }
    }

    @Test
    void referencesHaveTheExpectedShape() {
        for (int i = 0; i < 200; i++) {
            assertThat(ReferenceGenerator.next()).matches("SR-[A-HJKMNP-Z2-9]{8}");
        }
    }
}
