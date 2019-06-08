package com.comcom.server.security;

import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthFilter extends OncePerRequestFilter {
    public static final String ROLE_USER="user";//NO I18N
    public static final String ROLE_ADMIN="admin";//NO I18N
    private UserRepository userRepository;

    public AuthFilter(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getHeader("Authorization")==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"authorization not found");
            return;
        }
        String authorization = request.getHeader("Authorization");
        User data = userRepository.findFirstByToken(authorization);
        if(data==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"authorization not found");
            return;
        }
        List<SimpleGrantedAuthority> authorities=new ArrayList<SimpleGrantedAuthority>();
        if(data.isAdmin()){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+ROLE_USER));
            authorities.add(new SimpleGrantedAuthority("ROLE_"+ROLE_ADMIN));
        }else {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+ROLE_USER));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                data,
                data.getPassword(),
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
