package org.aqa;

import org.aqa.dto.ResponseMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.aqa.steps.WireMockSteps.verifyDoActionRequestQty;
import static org.aqa.util.Constants.TOKEN_NOT_FOUND_ERROR_MESSAGE;
import static org.aqa.util.Constants.VALIDATION_LOGIN_ERROR_MESSAGE;
import static org.aqa.util.RestActions.ACTION;
import static org.aqa.util.RestActions.LOGIN;

public class ActionTest extends BaseTest {
    @AfterEach
    void clearAuthRequestHistory() {
        removeServeEvents(postRequestedFor(urlPathMatching("/doAction")));
    }

    @Test
    void successDoActionTest() {
        String authToken = getAuthToken(true);
        getResponseMessage(authToken, LOGIN.name(), SC_OK);
        ResponseMessage responseMessage = getResponseMessage(authToken, ACTION.name(), SC_OK);

        assertResponse(responseMessage, null);
        verifyDoActionRequestQty(1);
    }

    @Test
    void notVerifiedTokenDoActionTest() {
        String authToken = getAuthToken(true);
        ResponseMessage responseMessage = getResponseMessage(authToken, ACTION.name(), SC_FORBIDDEN);

        assertResponse(responseMessage, format(TOKEN_NOT_FOUND_ERROR_MESSAGE, authToken));
        verifyDoActionRequestQty(0);
    }

    @ParameterizedTest(name = "{displayName}: token = {0}")
    @MethodSource("validationToken")
    @DisplayName("Validation token")
    void validationTokenDoActionRequestTest(String token) {
        ResponseMessage responseMessage = getResponseMessage(token, ACTION.name(), SC_BAD_REQUEST);

        assertResponse(responseMessage, VALIDATION_LOGIN_ERROR_MESSAGE);
        verifyDoActionRequestQty(0);
    }
}
