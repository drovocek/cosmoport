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
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "planet", required = false, defaultValue = "") String planet,
            @RequestParam(name = "isUsed", required = false, defaultValue = "Any") String isUsed,
            @RequestParam(name = "shipType", required = false, defaultValue = "Any") String shipType,
            @RequestParam(name = "after", required = false, defaultValue = "26160710400000") String after,
            @RequestParam(name = "before", required = false, defaultValue = "33134745600000") String before,
            @RequestParam(name = "minSpeed", required = false, defaultValue = "0") String minSpeed,
            @RequestParam(name = "maxSpeed", required = false, defaultValue = "1") String maxSpeed,
            @RequestParam(name = "minCrewSize", required = false, defaultValue = "0") String minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false, defaultValue = "10000") String maxCrewSize,
            @RequestParam(name = "minRating", required = false, defaultValue = "-1") String minRating,
            @RequestParam(name = "maxRating", required = false, defaultValue = "100") String maxRating,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") String pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "3") String pageSize,
            @RequestParam(name = "order", required = false, defaultValue = "ID") String order
    ) {
        return shipService.getShipsByFilterParam(
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
        ).getContent();
    }

    //Get ships count
    @GetMapping(path = "/rest/ships/count")
    public @ResponseBody
    Long getShipCount(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "planet", required = false, defaultValue = "") String planet,
            @RequestParam(name = "isUsed", required = false, defaultValue = "Any") String isUsed,
            @RequestParam(name = "shipType", required = false, defaultValue = "Any") String shipType,
            @RequestParam(name = "after", required = false, defaultValue = "26160710400000") String after,
            @RequestParam(name = "before", required = false, defaultValue = "33134745600000") String before,
            @RequestParam(name = "minSpeed", required = false, defaultValue = "0") String minSpeed,
            @RequestParam(name = "maxSpeed", required = false, defaultValue = "1") String maxSpeed,
            @RequestParam(name = "minCrewSize", required = false, defaultValue = "0") String minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false, defaultValue = "10000") String maxCrewSize,
            @RequestParam(name = "minRating", required = false, defaultValue = "-1") String minRating,
            @RequestParam(name = "maxRating", required = false, defaultValue = "100") String maxRating,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") String pageNumber
    ) {
        return shipService.getShipCount(
                name, planet,
                shipType,
                after, before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating
        );
    }

    //Create ship
    @PostMapping(path = "/rest/ships")
    public @ResponseBody
    Ship addNewShip(
            @RequestBody(required = false) Ship sample
    ) {
        return shipService.addNewShip(sample);
    }

    //Get ship
    @GetMapping("/rest/ships/{id}")
    public @ResponseBody
    Ship getShipById(
            @PathVariable String id
    ) {
        return shipService.getById(id);
    }

    //Update ship
    @PostMapping(path = "/rest/ships/{id}")
    public @ResponseBody
    Ship updateShipById(
            @PathVariable String id,
            @RequestBody(required = false) Ship sample
    ) {
        return shipService.updateShipById(id, sample);
    }

    //Delete ship
    @DeleteMapping(value = "/rest/ships/{id}")
    public @ResponseBody
    void deleteShipById(
            @PathVariable String id
    ) {
        shipService.deleteById(id);
    }
}