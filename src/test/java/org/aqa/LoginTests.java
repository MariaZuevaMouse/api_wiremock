package org.aqa;

import org.aqa.dto.ResponseMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.*;
import static org.aqa.steps.WireMockSteps.verifyAuthRequestQty;
import static org.aqa.util.Constants.LOGIN_REGISTERED_TOKEN_ERROR_MESSAGE;
import static org.aqa.util.Constants.VALIDATION_LOGIN_ERROR_MESSAGE;
import static org.aqa.util.RestActions.LOGIN;

public class LoginTests extends BaseTest {

    private static Stream<String> validationLogin() {
        return Stream.of("", "null", " ", getAuthToken(true).toLowerCase(), getAuthToken(true) + "1", getAuthToken(true).substring(0, 31), getAuthToken(false));
    }

    @AfterEach
    void clearAuthRequestHistory() {
        removeServeEvents(postRequestedFor(urlPathMatching("/auth")));
    }

    @ParameterizedTest(name = "{displayName}: token = {0}")
    @MethodSource("validationLogin")
    @DisplayName("Validation login")
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
        ResponseMessage responseMessage = getResponseMessage(authToken,
                LOGIN.name(), SC_CONFLICT);
        assertResponse(responseMessage, format(LOGIN_REGISTERED_TOKEN_ERROR_MESSAGE, authToken));
        verifyAuthRequestQty(0);
    }


//    private static ResponseMessage getResponseMessage(String token) {
//        ResponseMessage responseMessage = given().spec(specification)
//                .log().all()
//                .when().params(Map.of(ACTION_PARAM, LOGIN.name(),
//                        TOKEN_PARAM, token))
//                .post()
//                .prettyPeek()
//                .then()
//                .statusCode(400).extract().body().as(ResponseMessage.class);
//        return responseMessage;
//    }


//    @Test
//    void name() {
////        getAuthToken();
//        given().spec(specification)
////                .urlEncodingEnabled(false)
//                .log().all()
//                .when().params(Map.of(ACTION_PARAM, LOGIN.name(),
//                        TOKEN_PARAM, "ABC1ABCDE9ABCDEFABC1ABCDE9ABCDEF"))
////                .body(testPet)
//                .post()
//                .prettyPeek()
//                .then()
//                .statusCode(409);
//        List<ServeEvent> allServeEvents = getAllServeEvents();
//        System.out.println(allServeEvents);
//        verify(moreThan(5), postRequestedFor(urlEqualTo("/auth")));
//    }


}
