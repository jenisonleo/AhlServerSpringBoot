package com.comcom.server.security;

import com.comcom.server.bcrypt.BCryptPasswordEncoder;
import com.comcom.server.repository.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.File;
import java.io.FileInputStream;

import static com.comcom.server.security.AuthFilter.ROLE_ADMIN;
import static com.comcom.server.security.AuthFilter.ROLE_USER;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AhlAuthenticationProvider ahlAuthenticationProvider;

    @Value("${ahl.fcm.dburl}")
    private String fcmDbUrl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ahlAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                authorizeRequests()
                .antMatchers("/api/events").hasRole(ROLE_USER)
                .antMatchers("/api/infos").hasRole(ROLE_USER)
                .antMatchers("/api/event").hasRole(ROLE_ADMIN)
                .antMatchers("/api/info").hasRole(ROLE_ADMIN)
                .antMatchers("/notifications/register").hasRole(ROLE_USER)
                .anyRequest().authenticated();
        http
                .addFilterBefore(new AuthFilter(userRepository), BasicAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/login","/api/register","/noti");
    }

    @Bean
    BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    FirebaseApp get() throws Exception{
            File secuityFile=new File("src/main/resources/comcomapplication-firebase-adminsdk-i97ak-54b70ed958.json");
            if(!secuityFile.exists()){
                throw new RuntimeException("security file not found");
            }
            FileInputStream serviceAccount = new FileInputStream(secuityFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(fcmDbUrl)
                    .build();
            return FirebaseApp.initializeApp(options);
    }

}
