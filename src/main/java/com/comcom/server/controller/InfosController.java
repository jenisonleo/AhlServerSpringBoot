package com.comcom.server.controller;

import com.comcom.server.entity.Events;
import com.comcom.server.entity.Infos;
import com.comcom.server.repository.InfosRepository;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InfosController {

    private InfosRepository infosRepository;

    public InfosController(InfosRepository infosRepository){

        this.infosRepository = infosRepository;
    }


    @GetMapping(path = "/api/infos")
    public List<Infos> getEvents(){
        return infosRepository.findAll();
    }

    @PostMapping(path = "/api/info")
    public ResponseEntity<String> addNewInfo(Infos info){
        if(info.getCreatedAt()==0){
            info.setCreatedAt(System.currentTimeMillis());
        }
        if(info.getTitle()==null || info.getDescription()==null || info.getCreatedAt()<=0){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","required data not found");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.BAD_REQUEST);
        }
        Infos insertedinfo = infosRepository.insert(info);
        if (insertedinfo!=null) {
            System.out.println(insertedinfo.toString());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", "info added successfully");
            return new ResponseEntity<String>(jsonObject.toString(), null, HttpStatus.OK);
        }else {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("error","unable to add info");
            return new ResponseEntity<String>(jsonObject.toString(),null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
