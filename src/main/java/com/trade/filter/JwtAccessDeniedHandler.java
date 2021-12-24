package com.trade.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.constant.SecurityConstants;
import com.trade.dto.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle
            (HttpServletRequest request, HttpServletResponse response,
             AccessDeniedException accessDeniedException) throws IOException, ServletException {

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.setReason(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        httpResponse.setMessage(SecurityConstants.ACCESS_DENIED);
        httpResponse.setTimeStamp(new Date());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream,httpResponse);
        outputStream.flush();

    }
}
