package com.comcom.server.controller;

import com.comcom.server.Test;
import com.comcom.server.bcrypt.BCryptPasswordEncoder;
import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository){

        this.userRepository = userRepository;
    }

    @GetMapping(path = "/users")
    public String getUsers(){
        List<User> all = userRepository.findAll();
        all.forEach(new Consumer<User>() {
            @Override
            public void accept(User customer) {
                System.out.println("data"+" "+customer.getEmail());
            }
        });
        return "success"+all.size();
    }

    @PostMapping(path="/api/register",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> newUserRegistration(@RequestBody MultiValueMap<String,String> data){
        if(!(data.containsKey("username") && data.containsKey("fullname") && data.containsKey("email") && data.containsKey("password"))){
            return new ResponseEntity<String>("required param not found",null, HttpStatus.BAD_REQUEST);
        }
        Test test=new Test();
        test.fullname=data.getFirst(User.FULLNAME);
        test.email=data.getFirst(User.EMAIL);
        test.password=data.getFirst(User.PASSWORD);
        test.username=data.getFirst(User.USERNAME);

        System.out.println(data.toString());
        return ResponseEntity.ok("success");
    }


    @PostMapping(path = "/api/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody MultiValueMap<String,String> data){
        System.out.println("api");
        User username = userRepository.findFirstByUsername(data.getFirst("username"));
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(10);
        System.out.println(passwordEncoder.matches(data.getFirst("password"),username.getPassword()));
        return ResponseEntity.ok("success");
    }
}
