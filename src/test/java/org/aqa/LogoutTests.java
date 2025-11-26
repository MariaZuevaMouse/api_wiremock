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
import static org.aqa.util.Constants.TOKEN_NOT_FOUND_ERROR_MESSAGE;
import static org.aqa.util.Constants.VALIDATION_LOGIN_ERROR_MESSAGE;
import static org.aqa.util.RestActions.*;

public class LogoutTests extends BaseTest {

    @AfterEach
    void clearAuthRequestHistory() {
        removeServeEvents(postRequestedFor(urlPathMatching("/auth")));
    }

    @ParameterizedTest(name = "{displayName}: token = {0}")
    @MethodSource("validationToken")
    @DisplayName("Validation token")
    void validationLoginTest(String token) {
        ResponseMessage responseMessage = getResponseMessage(token, LOGOUT.name(), SC_BAD_REQUEST);

        assertResponse(responseMessage, VALIDATION_LOGIN_ERROR_MESSAGE);
        verifyAuthRequestQty(0);
    }

    @Test
    void logoutVerifiedTokenSuccessTest() {
        String authToken = getAuthToken(true);
        getResponseMessage(authToken, LOGIN.name(), SC_OK);
        ResponseMessage responseMessage = getResponseMessage(authToken, LOGOUT.name(), SC_OK);

        assertResponse(responseMessage, null);
    }

    @Test
    void logoutNotRegisteredTokenTest() {
        String authToken = getAuthToken(true);
        ResponseMessage responseMessage = getResponseMessage(authToken, LOGOUT.name(), SC_FORBIDDEN);

        assertResponse(responseMessage, format(TOKEN_NOT_FOUND_ERROR_MESSAGE, authToken));
    }
}
