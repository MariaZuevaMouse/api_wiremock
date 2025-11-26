package org.aqa;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.aqa.dto.ResponseMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.URLENC;
import static java.lang.String.format;
import static org.aqa.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BaseTest {

    static WireMockServer wireMockServer;
    public static final int port = 8888;
    static RequestSpecification specification;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(port));
        wireMockServer.start();
        WireMock.configureFor("localhost", port);

        RestAssured.baseURI = "http://localhost:8080/endpoint";//TestProperties.getTestDataProps().getProperty("baseUrl");
        specification = new RequestSpecBuilder()
                .setContentType(URLENC)
                .setAccept(ContentType.JSON)
                .setUrlEncodingEnabled(true)
                .addHeader("X-Api-Key", "qazWSXedc").build();

        stubFor(post(urlPathMatching("/auth"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                ));
        stubFor(post(urlPathMatching("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                ));
    }

    @AfterAll
    static void stopWiremock() {
        wireMockServer.stop();
    }

    static String getAuthToken(boolean isValid) {
        RgxGen rgxGen = RgxGen.parse("^[0-9A-F]{32}$");                     // Create generator
        String generatedToken = isValid ? rgxGen.generate() : rgxGen.generateNotMatching();
        log.info("generated token - {}", generatedToken);
        return generatedToken;
    }

    @Step(value = "Send request with params: token ={token}, action = {action}")
    protected static ResponseMessage getResponseMessage(String token, String action, int statusCode) {
        return step(format("Check status code = %s", statusCode), () ->
                given().spec(specification)
                        .when().params(Map.of(ACTION_PARAM, action, TOKEN_PARAM, token))
                        .log().all()
                        .post()
                        .prettyPeek()
                        .then()
                        .statusCode(statusCode)
                        .extract().body().as(ResponseMessage.class));
    }

    @Step("Check response body")
    void assertResponse(ResponseMessage responseMessage, String expectedErrorMessage) {
        if (expectedErrorMessage != null) {
            step(format("result == 'ERROR', message == '%s'", expectedErrorMessage), () -> {
                assertThat(responseMessage.getResult())
                        .as("result != %s", RESULT_ERROR)
                        .isEqualTo(RESULT_ERROR);
                assertThat(responseMessage.getMessage())
                        .as("message != %s", expectedErrorMessage)
                        .isEqualTo(expectedErrorMessage);
            });
        } else {
            step("result == OK, message == NULL", () -> {
                assertThat(responseMessage.getResult())
                        .as("result != %s", RESULT_OK)
                        .isEqualTo(RESULT_OK);
                assertThat(responseMessage.getMessage())
                        .as("message != null")
                        .isNull();
            });
        }
    }


//    @BeforeEach
//    void setUp() {
////        stubFor(post(urlPathMatching("/auth"))
////                .willReturn(aResponse()
////                                .withStatus(200)
////                                .withHeader("Content-Type", "application/json")
//////                        .withBody("\"testing-library\": \"WireMock\"")
////                ));
////        stubFor(post(urlPathMatching("/doAction"))
////                .willReturn(aResponse()
////                                .withStatus(200)
////                                .withHeader("Content-Type", "application/json")
//////                        .withBody("\"testing-library\": \"WireMock\"")
////                ));
//    }

//    @Test
//    void name() {
//        getValidToken();
//        given().spec(specification)
////                .urlEncodingEnabled(false)
//                .log().all()
//                .when().params(Map.of(ACTION_PARAM, LOGIN.name(),
//                        TOKEN_PARAM, "ABC1ABCDE9ABCDEFABC1ABCDE9ABCDEF"))

    /// /                .body(testPet)
//                .post()
//                .prettyPeek()
//                .then()
//                .statusCode(409);
//        List<ServeEvent> allServeEvents = getAllServeEvents();
//        System.out.println(allServeEvents);
//        verify(moreThan(5), postRequestedFor(urlEqualTo("/auth")));
//    }

    //    @RegisterExtension
//    static WireMockExtension wireMock = WireMockExtension.newInstance()
//            .options(wireMockConfig().port(8888))
//            .build();

//    static  String getInvalidToken(){
//        RgxGen rgxGen = RgxGen.parse("^[0-9A-F]{32}$");                     // Create generator
//        String token = rgxGen.generate();
//        log.info("generated token");
//        rgxGen.generateNotMatching();
//    }
}
