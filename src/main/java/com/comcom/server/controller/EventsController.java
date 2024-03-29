package com.comcom.server.controller;


import com.comcom.server.entity.Events;
import com.comcom.server.repository.EventsRepository;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EventsController {


    private EventsRepository eventsRepository;
    private NotificationsController notificationsController;

    public EventsController(EventsRepository eventsRepository , NotificationsController notificationsController){
        this.eventsRepository = eventsRepository;
        this.notificationsController = notificationsController;
    }



    @GetMapping(path = "/api/events")
    public List<Events> getEvents(){
        return eventsRepository.findAll();
    }

    @PostMapping(path = "/api/event")
    public ResponseEntity<String> addNewEvent(Events event){
        if(event.getTitle()==null || event.getDescription()==null || event.getPlace()==null || event.getFromDate()<=0|| event.getToDate()<=0){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","required data not found");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        if(event.getFromDate()>=event.getToDate()){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","FROM date cannot be higher than TO date");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        Events insertedEvent = eventsRepository.insert(event);
        if (insertedEvent!=null) {
            dispatchMessage(insertedEvent.getTitle(),insertedEvent.getDescription(),"new_event");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "event added successfully");
            return new ResponseEntity<String>(jsonObject.toString(), null, HttpStatus.OK);
        }else {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","unable to add event");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void dispatchMessage(String title,String info,String action){
        Map<String,String> message=new HashMap<>();
        message.put("title",title);
        message.put("message",info);
        message.put("action",action);
        notificationsController.dispatchMessage(message);
    }
}
