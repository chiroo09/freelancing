package com.laundryservice.maxcleaners.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundryservice.maxcleaners.config.JwtUtil;
import com.laundryservice.maxcleaners.exception.CustomException;
import com.laundryservice.maxcleaners.exception.ErrorResponse;
import com.laundryservice.maxcleaners.exception.InvalidTokenException;
import com.laundryservice.maxcleaners.exception.UnauthorizedException;
import com.laundryservice.maxcleaners.model.Customer;
import com.laundryservice.maxcleaners.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Author Tejesh
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private CustomerService customerService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // Extract the token from the request header
            String token = extractTokenFromRequest(request);
            logger.debug("Extracted token: {}", token);

            if (token != null && jwtUtil.isTokenValid(token, jwtUtil.extractPhoneNumber(token))) {
                // Extract phone number from the token
                String phoneNumber = jwtUtil.extractPhoneNumber(token);
                logger.debug("Extracted phone number: {}", phoneNumber);

                // Find the customer by phone number
                Customer customer = customerService.findByPhoneNumber(phoneNumber);
                logger.debug("Found customer: {}", customer);

                // Create authentication object
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        phoneNumber,
                        null,
                        new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("Successfully authenticated customer with phone number: {}", phoneNumber);
            }
        } catch (Exception ex) {
            // Catch all other exceptions and log the error
            logger.error("Unexpected error occurred during token processing: {}", ex.getMessage(), ex);
            // Prepare and return ErrorResponse
            ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Expired Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // Continue with the next filter in the chain
        chain.doFilter(request, response);
    }


    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
