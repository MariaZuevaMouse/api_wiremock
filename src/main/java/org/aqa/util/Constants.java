package org.aqa.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String ACTION_PARAM = "action";
    public static final String TOKEN_PARAM = "token";
    public static final String VALIDATION_LOGIN_ERROR_MESSAGE = "token: должно соответствовать \"^[0-9A-F]{32}$\"";
    public static final String LOGIN_REGISTERED_TOKEN_ERROR_MESSAGE = "Token '%s' already exists";
    public static final String TOKEN_NOT_FOUND_ERROR_MESSAGE = "Token '%s' not found";
    public static final String RESULT_OK = "OK";
    public static final String RESULT_ERROR = "ERROR";
}
