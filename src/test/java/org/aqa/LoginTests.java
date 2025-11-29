package org.aqa;

import org.aqa.dto.ResponseMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.*;
import static org.aqa.steps.WireMockSteps.verifyAuthRequestQty;
import static org.aqa.util.Constants.LOGIN_REGISTERED_TOKEN_ERROR_MESSAGE;
import static org.aqa.util.Constants.VALIDATION_LOGIN_ERROR_MESSAGE;
import static org.aqa.util.RestActions.LOGIN;

public class LoginTests extends BaseTest {

    @AfterEach
    void clearAuthRequestHistory() {
        removeServeEvents(postRequestedFor(urlPathMatching("/auth")));
    }

    @ParameterizedTest(name = "{displayName}: token = {0}")
    @MethodSource("validationToken")
    @DisplayName("Validation token")
    void validationLoginTest(String token) {
        ResponseMessage responseMessage = getResponseMessage(token, LOGIN.name(), SC_BAD_REQUEST);
        assertResponse(responseMessage, VALIDATION_LOGIN_ERROR_MESSAGE);
        verifyAuthRequestQty(0);
    }

    @Test
    void successLoginTest() {
        ResponseMessage responseMessage = getResponseMessage(getAuthToken(true), LOGIN.name(), SC_OK);
        assertResponse(responseMessage, null);
        verifyAuthRequestQty(1);
    }

    @Test
    void registeredTokenLoginTest() {
        String authToken = getAuthToken(true);
        getResponseMessage(authToken, LOGIN.name(), SC_OK);
        removeServeEvents(postRequestedFor(urlPathMatching("/auth")));
        ResponseMessage responseMessage = getResponseMessage(authToken, LOGIN.name(), SC_CONFLICT);

        assertResponse(responseMessage, format(LOGIN_REGISTERED_TOKEN_ERROR_MESSAGE, authToken));
        verifyAuthRequestQty(0);
    }

}
