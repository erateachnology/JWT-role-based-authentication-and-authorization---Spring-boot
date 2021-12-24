package com.trade.service.impl;

import com.trade.constant.SecurityConstants;
import com.trade.dto.ResetPasswordRequest;
import com.trade.enums.Role;
import com.trade.exception.*;
import com.trade.model.Otp;
import com.trade.model.User;
import com.trade.repository.OtpRepository;
import com.trade.repository.UserRepository;
import com.trade.service.UserService;
import com.trade.service.emailService.EmailService;
import com.trade.service.securityService.UserPrincipal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String USER_NAME_ALREADY_EXIST = "User Name already exist";
    private static final String EMAIL_ALREADY_EXIST = "Email already exist";
    private static final String USER_NOT_FOUND_BY_USERNAME_EX = "User not found by username";
    private static final String USER_NOT_FOUND_BY_USERNAME = USER_NOT_FOUND_BY_USERNAME_EX + " {}";
    private static final String USER_FOUND_BY_USERNAME = "User found by username {}";
    private static final String USER_NOT_FOUND_BY_EMAIL = "User not found by email";
    private static final String ENTERED_OTP_NOT_CORRECT = "Entered OTP not correct, Please enter your OTP again";
    private static final String ENTERED_OTP_EXPIRED = "Entered OTP expired, Please try again";
    private static final String ENTERED_OTP_IS_NOT_ACTIVE = "Entered OTP is not active, Please try again";
    private static final String YOUR_PASSWORD_RESET_SUCCESSFULLY = "Your password reset successfully";
    private static final String OTP_SEND_SUCCESSFULLY = "OTP send successfully";
    private static final String USER_NOT_FOUND_FOR_THIS_USERNAME = "User not found for this username";
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUserName(username);
        if (user == null) {
            logger.error(USER_NOT_FOUND_BY_USERNAME, username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME_EX + " " + username);
        } else {
            logger.error(USER_FOUND_BY_USERNAME, username);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            return new UserPrincipal(user);
        }
    }


    @Override
    public User register(String firstName, String lastName, String userName,
                         String email, String password,
                         String countryCode, String number)
            throws UserNotFoundException, EmailExistException, UserNameExistException {
        validateUserNameAndEmail(StringUtils.EMPTY, userName,email);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCountryCode(countryCode);
        user.setNumber(number);
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole("USER");
        return userRepository.save(user);
    }

    private User validateUserNameAndEmail(String currentUserName, String newUserName, String email)
            throws UserNotFoundException, EmailExistException, UserNameExistException {
        User newUser = findByUserName(newUserName);
        User userByEmail = findUserByEmail(email);
        if (StringUtils.isNotBlank(currentUserName)) {
            User currentUser = findByUserName(currentUserName);

            if (currentUser == null) {
                logger.error(USER_NOT_FOUND_FOR_THIS_USERNAME);
                throw new UserNotFoundException("User not found for this username:"+ currentUserName);
            }

            if (newUser != null && newUser.getId().equals(currentUser.getId())) {
                logger.error(USER_NAME_ALREADY_EXIST);
                throw new UserNameExistException(USER_NAME_ALREADY_EXIST);
            }

            if (userByEmail != null && userByEmail.getId().equals(currentUser.getId())) {
                logger.error(EMAIL_ALREADY_EXIST);
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;
        }else {
            if (newUser != null) {
                logger.error(USER_NAME_ALREADY_EXIST);
                throw new UserNameExistException(USER_NAME_ALREADY_EXIST);
            }
            if (userByEmail != null) {
                logger.error(EMAIL_ALREADY_EXIST);
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return null;
        }
    }
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findUserByUserName(userName);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    @Override
    public String sendOtp(User user) throws UserNotFoundException, MessagingException {
        User userByEmail = findUserByEmail(user.getEmail());
        if(userByEmail == null){
            logger.error(USER_NOT_FOUND_BY_EMAIL);
            throw new UserNotFoundException(USER_NOT_FOUND_BY_EMAIL);
        }else{
            //generate otp
            String otpNumber =  RandomStringUtils.randomNumeric(6);
            //save otp
           Otp otp = saveOtp(otpNumber, userByEmail);
            //send email with the otp
            emailService.sendOtpMail(userByEmail.getUserName(), otpNumber, user.getEmail());
        }
        return OTP_SEND_SUCCESSFULLY;
    }


    @Override
    public String resetPassword(ResetPasswordRequest request) throws OtpNotFoundException, OtpExpiredException, OtpInActiveException {
        Otp otp = otpRepository.findOtpByOtp(request.getOtpNumber());
        User user = null;
        if(otp == null){
            logger.error(ENTERED_OTP_NOT_CORRECT);
            throw new OtpNotFoundException(ENTERED_OTP_NOT_CORRECT);
        }else{
            if(otp.getExpiredAt().before(new Date())){
                otp.setActive(false);
                otpRepository.save(otp);
                logger.error(ENTERED_OTP_EXPIRED);
             throw new OtpExpiredException(ENTERED_OTP_EXPIRED);
            } else if(!otp.isActive()){
                logger.error(ENTERED_OTP_IS_NOT_ACTIVE);
                throw new OtpInActiveException(ENTERED_OTP_IS_NOT_ACTIVE);
            }else {
                user = userRepository.getById(otp.getUserId());
                if(request.getNewPassword().equalsIgnoreCase(request.getConfirmPassword())){
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(user);
                }
                otp.setActive(false);
                otpRepository.save(otp);
            }
        }
        return YOUR_PASSWORD_RESET_SUCCESSFULLY;
    }

    private Otp saveOtp(String otpNumber, User userByEmail) {
        Otp otp = new Otp();
        otp.setOtp(otpNumber);
        otp.setActive(true);
        otp.setCreatedAt(new Date());
        otp.setUserId(userByEmail.getId());
        otp.setUserEmail(userByEmail.getEmail());
        otp.setExpiredAt(new Date(System.currentTimeMillis() + SecurityConstants.OTP_EXP_TIME));
        return  otpRepository.save(otp);
    }
}
