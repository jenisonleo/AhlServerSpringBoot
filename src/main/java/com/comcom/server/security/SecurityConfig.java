package com.comcom.server.security;

import com.comcom.server.bcrypt.BCryptPasswordEncoder;
import com.comcom.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.comcom.server.security.AuthFilter.ROLE_ADMIN;
import static com.comcom.server.security.AuthFilter.ROLE_USER;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository ahlUserDetailService;
    @Autowired
    private AhlAuthenticationProvider ahlAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("configure"+ahlAuthenticationProvider.toString());
        auth.authenticationProvider(ahlAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                authorizeRequests()
                .antMatchers("/api/events").hasRole(ROLE_ADMIN)
                .antMatchers("/api/infos").hasRole(ROLE_USER)
                .anyRequest().authenticated();
        http
                .addFilterBefore(new AuthFilter(ahlUserDetailService), BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/login","/api/register");
    }

    @Bean
    BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
