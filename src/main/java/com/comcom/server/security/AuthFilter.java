package com.comcom.server.security;

import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class AuthFilter extends OncePerRequestFilter {

    private AhlUserDetailService userRepository;

    public AuthFilter(AhlUserDetailService userRepository){
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("data");
        if(request.getHeader("Authorization")==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"authorization not found");
            return;
        }
        String authorization = request.getHeader("Authorization");
        User data = userRepository.userRepository.findFirstByToken(authorization);
        if(data==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"authorization not found");
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                data,
                null,
                userRepository.loadUserByUsername(data.getUsername()).getAuthorities()
        );
//        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
