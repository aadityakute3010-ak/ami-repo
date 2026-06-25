package com.ami.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ami.repository.BlacklistedTokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;
    
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,CustomUserDetailsService userDetailsService,
    		BlacklistedTokenRepository blacklistedTokenRepository) {
    	
        this.jwtUtil = jwtUtil; 

        this.userDetailsService = userDetailsService;

        this.blacklistedTokenRepository = blacklistedTokenRepository;
    } 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
    		FilterChain filterChain) 
    				throws ServletException, IOException {
 
        final String authHeader =
                request.getHeader("Authorization");

        System.out.println("AUTH HEADER: " + authHeader);

        String email = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);
            
            if (blacklistedTokenRepository.existsByToken(token) ) {
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                        "Token has been logged out"
                );

                return;
            }

            System.out.println("TOKEN: " + token);

            email = jwtUtil.extractEmail(token);

            System.out.println("EMAIL FROM TOKEN: " + email);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

                System.out.println("USER AUTHENTICATED");
            }
        } 

        filterChain.doFilter(request, response);
    }
    
}