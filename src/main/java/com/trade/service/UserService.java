package com.trade.service;

import com.trade.dto.ResetPasswordRequest;
import com.trade.exception.*;
import com.trade.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String userName, String email, String password,
                  String countryCode, String number)
            throws UserNotFoundException, EmailExistException, UserNameExistException;

    List<User> getUsers();

    User findByUserName(String userName);

    User findUserByEmail(String email);
    UserDetails loadUserByUsername(String username);

    String sendOtp(User user) throws UserNotFoundException, MessagingException;

    String resetPassword(ResetPasswordRequest request) throws OtpNotFoundException, OtpExpiredException, OtpInActiveException;
}
