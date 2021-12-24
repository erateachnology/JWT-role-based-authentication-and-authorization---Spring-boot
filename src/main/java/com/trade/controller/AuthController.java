package com.trade.controller;


import com.trade.constant.SecurityConstants;
import com.trade.dto.ResetPasswordRequest;
import com.trade.exception.*;
import com.trade.model.User;
import com.trade.service.UserService;
import com.trade.utility.JwtTokenProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthController extends ExceptionHandelling {
    private static final String LOG_OUT_SUCCESSFULLY = "Log out successfully";
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user)
            throws UserNotFoundException, EmailExistException, UserNameExistException {
        User registeredUser = userService.register(user.getFirstName(),
                user.getLastName(), user.getUserName(), user.getEmail(), user.getPassword(),
                user.getCountryCode(), user.getNumber());
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user)
            throws UserNotFoundException, EmailExistException, UserNameExistException {
        authenticate(user.getUserName(),user.getPassword());
        UserDetails loginUser = userService.loadUserByUsername(user.getUserName());
        //UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeaders(loginUser);
        return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeaders(UserDetails userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstants.JWT_TOK_HEADER , jwtTokenProvider.generateToken(userPrincipal));
        return httpHeaders;
    }

    private void authenticate(String userName, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName,password));
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
                                 HttpServletRequest request, HttpServletResponse response) {
       // Delete token if you store somewhere else, Here No any storage due to security;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        //WebUtil.removeCookie(response, HttpHeaders.AUTHORIZATION);
        request.getSession().invalidate();
      return ResponseEntity.ok("logout");
    }

    @PostMapping("/forgetPassword/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestBody User user) throws UserNotFoundException, MessagingException {
       return ResponseEntity.ok(userService.sendOtp(user));
    }

    @PostMapping("/forgetPassword/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) throws OtpNotFoundException, OtpExpiredException, OtpInActiveException {
        return ResponseEntity.ok(userService.resetPassword(request));
    }

}
