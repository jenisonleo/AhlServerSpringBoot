package com.comcom.server.controller;


import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import com.google.common.collect.Multimap;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RestController
public class NotificationsController {
    public static final String DEVICE_TOKEN="devicetoken";
    public static final String DEVICE_TYPE="devicetype";
    private UserRepository userRepository;

    public NotificationsController(UserRepository userRepository){

        this.userRepository = userRepository;
    }

    @PostMapping(path = "/notifications/register",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> addNotificationRegistration(@RequestBody MultiValueMap<String,String> data, Authentication authentication){
        if(!(data.containsKey(DEVICE_TOKEN) && data.containsKey(DEVICE_TYPE))){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","required param not found");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        String token = data.getFirst(DEVICE_TOKEN);
        String deviceType = data.getFirst(DEVICE_TYPE);
        User.OsType osType;
        if(deviceType.equals("android")){
            osType= User.OsType.android;
        }else if(deviceType.equals("ios")){
            osType= User.OsType.ios;
        }else {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","unable to recongnize device type");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        String username = ((User) authentication.getPrincipal()).getUsername();
        List<User> results = userRepository.findbyDeviceId(token);
        if(results.size()>0) {
            results.forEach(datas-> datas.getNotificationDetails().getDetailslist().removeIf(deviceTokenParams -> deviceTokenParams.getDeviceId().equals(token)));
            userRepository.saveAll(results);
        }
        User dbdata = userRepository.findFirstByUsername(username);
        User.NotificationDetails notificationDetails = dbdata.getNotificationDetails();
        if(notificationDetails==null){
            notificationDetails=new User.NotificationDetails();
            notificationDetails.setDetailslist(new ArrayList<>());
            dbdata.setNotificationDetails(notificationDetails);
        }
        User.NotificationDetails.DeviceTokenParams deviceTokenParams = new User.NotificationDetails.DeviceTokenParams();
        deviceTokenParams.setDeviceId(token);
        deviceTokenParams.setOsType(osType);
        if(notificationDetails.getDetailslist()==null){
            notificationDetails.setDetailslist(new ArrayList<>());
        }
        notificationDetails.getDetailslist().add(deviceTokenParams);
        User savedResult = userRepository.save(dbdata);
        for(User.NotificationDetails.DeviceTokenParams ds:savedResult.getNotificationDetails().getDetailslist()){
            if(ds.getDeviceId().equals(token)){
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("token",ds.getDeviceId());
                jsonObject.addProperty("username",savedResult.getUsername());
                jsonObject.addProperty("message","notification registered Successfully");
                return new ResponseEntity<String>(jsonObject.toString(),HttpStatus.OK);//NO I18N
            }
        }
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("error","unable to register");
        return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    @PostMapping(path = "/notifications/deregister",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public ResponseEntity<String> removeNotificationRegistration(@RequestBody MultiValueMap<String,String> data, Authentication authentication) {
//
//    }

    @GetMapping(value = "/noti")
    public String sendmess(){
        List<User> allDeviceId = userRepository.findAllDeviceId();
        ArrayList<String> data=new ArrayList<>();
        for(User user:allDeviceId){
            if(user.getNotificationDetails()!=null){
                if(user.getNotificationDetails().getDetailslist()!=null){
                    for(User.NotificationDetails.DeviceTokenParams params:user.getNotificationDetails().getDetailslist()){
                        data.add(params.getDeviceId());
                    }
                }
            }
        }
        ArrayList<String> testData=new ArrayList<>();
        ArrayList<String> csv=new ArrayList<>();
        for(int i=0;i<204;i++){
            testData.add("datan_"+i);
        }
        System.out.println("size "+data.size());
        Message message = Message.builder()
                .putData("message", "hi")
                .setToken("testtoken")
                .build();

// Send a message to the device corresponding to the provided
// registration token.
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("fcm "+response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return "succees";
    }
}
