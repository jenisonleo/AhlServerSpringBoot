package com.comcom.server.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.comcom.server.Test;
import com.comcom.server.bcrypt.BCryptPasswordEncoder;
import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
public class UserController {
    public static final String PASSWORD="password";
    public static final String USERNAME="username";
    public static final String FULLNAME="fullname";
    public static final String EMAIL="email";



    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${ahl.jwt.secret}")
    private String secretkey;

    public UserController(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path="/api/register",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> newUserRegistration(@RequestBody MultiValueMap<String,String> data){
        if(!(data.containsKey(USERNAME) && data.containsKey(FULLNAME) && data.containsKey(EMAIL) && data.containsKey(PASSWORD))){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","required param not found");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        User oldUsernameEntry = userRepository.findFirstByUsername(data.getFirst(USERNAME));
        if(oldUsernameEntry!=null){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","username already taken");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.CONFLICT);
        }
        if(!User.isValidEmailAddress(data.getFirst(EMAIL))){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","email id is not valid");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.CONFLICT);
        }
        if(data.getFirst(PASSWORD).length()<8){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","password should contain minimum 8 characters");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.CONFLICT);
        }
        User oldEmailEntry = userRepository.findFirstByEmail(data.getFirst(EMAIL));
        if(oldEmailEntry!=null){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","email already registered");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.CONFLICT);
        }
        User user=new User();
        user.setFullname(data.getFirst(FULLNAME));
        user.setEmail(data.getFirst(EMAIL));
        user.setPassword(passwordEncoder.encode(data.getFirst(PASSWORD)));
        user.setUsername(data.getFirst(USERNAME));
        String secretKey = createSecretKey(user.getUsername(), user.getPassword());
        user.setToken(secretKey);
        User savedResp = userRepository.save(user);
        if(savedResp==null){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","unable to register this suer");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("token",savedResp.getToken());
        jsonObject.addProperty("name",savedResp.getFullname());
        jsonObject.addProperty("email",savedResp.getEmail());
        jsonObject.addProperty("isAdmin",savedResp.isAdmin());
        jsonObject.addProperty("message","User registered Successfully");
        return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.OK);//NO I18N
    }


    @PostMapping(path = "/api/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody MultiValueMap<String,String> data){
        if(!data.containsKey("username") || !data.containsKey("password")){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","username and password is required");
            return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.BAD_REQUEST);//NO I18N
        }
        User username = userRepository.findFirstByUsername(data.getFirst("username"));
        if(username==null){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","username not found");
            return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.BAD_REQUEST);//NO I18N
        }
        if(!(passwordEncoder.matches(data.getFirst("password"),username.getPassword()))){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","incorrect password");
            return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.BAD_REQUEST);//NO I18N
        }
        String key=createSecretKey(username.getId().toString(),username.getPassword());
        username.setToken(key);
        User savedResp = userRepository.save(username);
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("token",savedResp.getToken());
        jsonObject.addProperty("name",savedResp.getFullname());
        jsonObject.addProperty("email",savedResp.getEmail());
        jsonObject.addProperty("isAdmin",savedResp.isAdmin());
        jsonObject.addProperty("message","login successful");
        return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.OK);//NO I18N
    }

    private String createSecretKey(String userObjectId,String encryptedPassword){
        return JWT.create().withClaim(userObjectId, encryptedPassword).sign(Algorithm.HMAC256(secretkey));
    }
}
