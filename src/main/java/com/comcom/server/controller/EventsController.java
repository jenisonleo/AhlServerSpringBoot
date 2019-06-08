package com.comcom.server.controller;


import com.comcom.server.entity.Events;
import com.comcom.server.repository.EventsRepository;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventsController {


    private EventsRepository eventsRepository;

    public EventsController(EventsRepository eventsRepository){

        this.eventsRepository = eventsRepository;
    }



    @GetMapping(path = "/api/events")
    public List<Events> getEvents(){
        return eventsRepository.findAll();
    }
}
