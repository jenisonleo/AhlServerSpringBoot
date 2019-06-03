package com.comcom.server.security;

import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AhlUserDetailService implements UserDetailsService {
    private UserRepository userRepository;

    public  AhlUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User firstByUsername = userRepository.findFirstByUsername(username);
        List<SimpleGrantedAuthority> data=new ArrayList<SimpleGrantedAuthority>();
        data.add(new SimpleGrantedAuthority("user"));
        return new org.springframework.security.core.userdetails.User(firstByUsername.getUsername(),firstByUsername.getPassword(),data);
    }
}
