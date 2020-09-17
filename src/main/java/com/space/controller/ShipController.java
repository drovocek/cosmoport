package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@EnableJpaRepositories
public class ShipController {
    @Autowired
    private ShipService shipService;

    //Get ships list
    @GetMapping(path = "/rest/ships")
    public @ResponseBody
    Iterable<Ship> getShips(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "planet", required = false) String planet,
            @RequestParam(name = "isUsed", required = false) String isUsed,
            @RequestParam(name = "shipType", required = false) String shipType,
            @RequestParam(name = "after", required = false) String after,
            @RequestParam(name = "before", required = false) String before,
            @RequestParam(name = "minSpeed", required = false) String minSpeed,
            @RequestParam(name = "maxSpeed", required = false) String maxSpeed,
            @RequestParam(name = "minCrewSize", required = false) String minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false) String maxCrewSize,
            @RequestParam(name = "minRating", required = false) String minRating,
            @RequestParam(name = "maxRating", required = false) String maxRating,
            @RequestParam(name = "pageNumber", required = false) String pageNumber,
            @RequestParam(name = "pageSize", required = false) String pageSize,
            @RequestParam(name = "order", required = false) String order
    ) {
        System.out.println("!!!!!getShips!!!!");
        System.out.println(isUsed);

        List<Ship> allShips = shipService.getShipsByFilterParam(
                name, planet,
                shipType,
                after, before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,
                pageNumber,
                pageSize,
                order
        );

        return allShips;
    }

    //Get ships count
    @GetMapping(path = "/rest/ships/count")
    public @ResponseBody
    long getShipCount() {
        System.out.println("!!!!!getShipCount!!!!");
        return shipService.getShipCount();
    }

    //Create ship
    @PostMapping(path = "/rest/ships")
    public @ResponseBody
    Ship addNewShip(
            @RequestBody Ship sample
    ) {
        System.out.println("!!!!!addNewShip!!!!");
        return shipService.addNewShip(sample);
    }

    //Get ship
    @GetMapping("/rest/ships/{id}")
    public @ResponseBody
    Ship getShipById(
            @PathVariable Long id
    ) {
        System.out.println("!!!!!getShipById!!!!");
        return shipService.getById(id);
    }

    //Update ship
    @PostMapping(path = "/rest/ships/{id}")
    public @ResponseBody
    Ship updateShipById(
            @PathVariable Long id,
            @RequestBody Ship sample
    ) {
        System.out.println("!!!!!updateShipById!!!!");
        return shipService.updateShipById(id, sample);
    }

    //Delete ship
    @DeleteMapping(value = "/rest/ships/{id}")
    public @ResponseBody
    void deleteShipById(
            @PathVariable Long id
    ) {
        //TODO добавить метод проверки валидности id
        System.out.println("!!!!!deleteShipById!!!!");
        shipService.deleteById(id);
    }
}