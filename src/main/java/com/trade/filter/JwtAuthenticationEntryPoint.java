package com.trade.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.constant.SecurityConstants;
import com.trade.dto.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        httpResponse.setHttpStatus(HttpStatus.FORBIDDEN);
        httpResponse.setReason(HttpStatus.FORBIDDEN.getReasonPhrase());
        httpResponse.setMessage(SecurityConstants.FORBIDDEN_MESSAGE);
        httpResponse.setTimeStamp(new Date());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream,httpResponse);
        outputStream.flush();
    }
}
