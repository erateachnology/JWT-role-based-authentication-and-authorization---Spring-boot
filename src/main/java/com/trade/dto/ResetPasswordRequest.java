package com.trade.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String  otpNumber;
    private String newPassword;
    private String confirmPassword;
}
