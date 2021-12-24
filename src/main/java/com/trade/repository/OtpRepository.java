package com.trade.repository;

import com.trade.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findOtpByOtp(String otpNumber);
}
