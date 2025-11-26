package org.aqa.steps;

import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WireMockSteps {

    @Step("Verify post request to mock /auth qty =  {count}")
    public static void verifyAuthRequestQty(int count){
        verify(count, postRequestedFor(urlEqualTo("/auth")));
    }

    @Step("Verify post request to mock /doAction qty =  {count}")
    public static void verifyDoActionRequestQty(int count){
        verify(count, postRequestedFor(urlEqualTo("/auth")));
    }
}
