package com.trade.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.trade.dto.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.Objects;

@ControllerAdvice
public class ExceptionHandelling {
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandelling.class);
    private static final String ACCOUNT_LOCKED = "This account has been locked, Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint, Please send a '%s' request";
    private static  final String INTERNAL_SERVER_ERROR = "An error occurred while processing this request";
    private static  final String INCORRECT_CREDENTIALS = "Username / Password incorrect, Please try again";
    private static  final String ACCOUNT_DISABLED = "This account has been disabled, Please contact administration";
    private static  final String NOT_ENOUGH_PERMISSIONS = "You don`t have enough permissions";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(){
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSIONS);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException(){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(OtpInActiveException.class)
    public ResponseEntity<HttpResponse> otpInActiveException(OtpInActiveException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<HttpResponse> otpNotFoundException(OtpNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }


    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<HttpResponse> otpExpiredException(OtpExpiredException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(UserNameExistException.class)
    public ResponseEntity<HttpResponse> userNameExistException(UserNameExistException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> nohandelerFoundException(NoHandlerFoundException exception){
        return createHttpResponse(HttpStatus.BAD_REQUEST, "This page was not found".toUpperCase());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> requestMethodNotFoundException(HttpRequestMethodNotSupportedException exception){

        HttpMethod supportedMethod  = Objects.requireNonNull(exception.getSupportedHttpMethods().iterator().next());
        return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerError(Exception exception){
        logger.error(exception.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setTimeStamp(new Date());
        httpResponse.setHttpStatus(httpStatus);
        httpResponse.setStatusCode(httpStatus.value());
        httpResponse.setReason(httpStatus.getReasonPhrase().toUpperCase());
        httpResponse.setMessage(message.toUpperCase());

        return new ResponseEntity<>(httpResponse,httpStatus);
    }
}
