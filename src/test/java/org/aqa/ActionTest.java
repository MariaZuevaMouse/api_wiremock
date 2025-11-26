package org.aqa;

import org.junit.jupiter.api.AfterEach;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ActionTest extends BaseTest {
    @AfterEach
    void clearAuthRequestHistory() {
        removeServeEvents(postRequestedFor(urlPathMatching("/doAction")));
    }
}
