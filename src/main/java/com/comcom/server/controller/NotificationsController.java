package com.comcom.server.controller;


import com.comcom.server.entity.User;
import com.comcom.server.repository.UserRepository;
import com.google.firebase.messaging.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        removeDeviceToken(token);
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

    private void removeDeviceToken(String token) {
        List<User> results = userRepository.findbyDeviceId(token);
        if(results.size()>0) {
            results.forEach(datas-> datas.getNotificationDetails().getDetailslist().removeIf(deviceTokenParams -> deviceTokenParams.getDeviceId().equals(token)));
            userRepository.saveAll(results);
        }
    }


//    @PostMapping(path = "/notifications/deregister",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public ResponseEntity<String> removeNotificationRegistration(@RequestBody MultiValueMap<String,String> data, Authentication authentication) {
//
//    }

    @GetMapping(value = "/noti")
    public String sendmess()throws FirebaseMessagingException{
        Map<String,String> message=new HashMap<>();
        message.put("message","jenison");
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
        int start=-1;

        while(true) {
            int from=start+1;
            int to=start+100;
            start+=100;
            if(to>=data.size()-1){
                to=data.size()-1;
                if(to>=from) {
                    sendmessage(from, to, data,message);
                }
                break;
            }
            if(to>=from) {
                sendmessage(from, to, data,message);
            }
        }
        System.out.println("size "+data.size());
        return "succees";
    }

    private void sendmessage(int from, int to, ArrayList<String> data, Map<String, String> messageToSend) throws FirebaseMessagingException{
        MulticastMessage message = MulticastMessage.builder()
                .putAllData(messageToSend)
                .addAllTokens(data.subList(from,to+1))
                .build();
        BatchResponse res = FirebaseMessaging.getInstance().sendMulticast(message);
        for(int i=0;i<res.getResponses().size();i++){
            SendResponse response = res.getResponses().get(i);
            if(response.getException()!=null){
                if(response.getException().getErrorCode().equals("registration-token-not-registered") || response.getException().getErrorCode().equals("invalid-argument")){
                    removeDeviceToken(data.get(from+i));
                }
            }
        }
    }
}
