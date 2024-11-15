package com.ltjeda.web.app.iobank.filter;

import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        if(jwtToken == null || !jwtService.isTokenValid(jwtToken.substring(7))){
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        String subject = jwtService.extractSubject(jwtToken);
        User user = (User) userDetailsService.loadUserByUsername(subject);
        SecurityContext context = SecurityContextHolder.getContext();
        if(user != null && context.getAuthentication() == null){
            var authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authenticationToken.setDetails(request);
            context.setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
