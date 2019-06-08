package com.comcom.server.controller;

import com.comcom.server.entity.Infos;
import com.comcom.server.repository.InfosRepository;
import org.springframework.web.bind.annotation.GetMapping;
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
}
