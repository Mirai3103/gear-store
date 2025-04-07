package com.ecom.config;

import java.io.IOException;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthFailureHandlerImpl(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
                                        throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = userRepository.findByEmail(email);
        log.info(exception.getMessage());
        if (user == null) {
            // user không tồn tại
            exception = new UsernameNotFoundException("Email or password invalid");
        } 
        // Nếu bạn có logic khác (failedAttempt) thì để ở đây
        // else { ... }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
