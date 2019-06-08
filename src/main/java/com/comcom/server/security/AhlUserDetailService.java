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

import static com.comcom.server.security.AuthFilter.ROLE_ADMIN;
import static com.comcom.server.security.AuthFilter.ROLE_USER;

@Service
public class AhlUserDetailService implements UserDetailsService {


    public UserRepository userRepository;

    public  AhlUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findFirstByUsername(username);
        List<SimpleGrantedAuthority> data=new ArrayList<SimpleGrantedAuthority>();
        if(user.isAdmin()){
            data.add(new SimpleGrantedAuthority("ROLE_"+ROLE_USER));
            data.add(new SimpleGrantedAuthority("ROLE_"+ROLE_ADMIN));
        }else {
            data.add(new SimpleGrantedAuthority("ROLE_"+ROLE_USER));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),data);
    }
}
