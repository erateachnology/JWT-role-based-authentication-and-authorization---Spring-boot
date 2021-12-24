package com.trade.constant;

public class SecurityConstants {

    public static final long Tok_EXP_TIME = 432_000_000;
    public static final long OTP_EXP_TIME = 300_000;
    public static final String TOK_PREFIX = "Bearer ";
    public static final String JWT_TOK_HEADER = "Jwt-Token";
    public static final String TOK_CANNOT_BE_VERIFIED = "Token cannot be verified";
    //Issuer name
    public static final String GET_ARRAYS_LLC = "Trade";
    //Audience name
    public static final String GET_ARRAYS_ADMINISTRATION = "Trade";

    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String OPTIONS_HTTP_METHODS = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/resetpassword/**", "/user/image/**"};
}
