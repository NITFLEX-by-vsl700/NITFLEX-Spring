package com.vsl700.nitflex.configs;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsl700.nitflex.exceptions.CustomException;
import com.vsl700.nitflex.exceptions.UnauthorizedException;
import com.vsl700.nitflex.models.dto.ErrorDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String userAgentHeader = httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

        if (authHeader != null) {
            String[] authElements = authHeader.split(" ");

            if (authElements.length == 2
                    && "Bearer".equals(authElements[0])) {
                try {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthenticationProvider.createAuthentication(authElements[1], userAgentHeader));
                } catch (JWTVerificationException e) {
                    SecurityContextHolder.clearContext();

                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    OBJECT_MAPPER.writeValue(httpServletResponse.getOutputStream(), new ErrorDto("Token verification failed!"));
                    return;
                } catch (CustomException e){
                    SecurityContextHolder.clearContext();

                    httpServletResponse.setStatus(e.getStatusCode().value());
                    OBJECT_MAPPER.writeValue(httpServletResponse.getOutputStream(), new ErrorDto(e.getMessage()));
                    return;
                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
