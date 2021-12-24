package com.trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
@Data
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/New_York")
    private Date timeStamp;
    private int statusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String message;
}
