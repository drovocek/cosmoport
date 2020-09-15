package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.model.errors.IllegalIdException;
import com.space.model.errors.ShipNotFoundException;
import com.space.repository.ShipRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Controller
public class ShipController {
    @Autowired
    private ShipRepository shipRepository;
    private ArrayList<Ship> allShips;
    private long count;

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public @ResponseBody
    Iterable<Ship> getShips(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "planet", required = false) String planet,
            @RequestParam(name = "shipType", required = false) String shipType,
            @RequestParam(name = "after", required = false) String after,
            @RequestParam(name = "before", required = false) String before,
            @RequestParam(name = "isUsed", required = false) String isUsed,
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
        String sName = (name == null) ? "" : name;
        String pName = (planet == null) ? "" : planet;
        String sType = (shipType == null) ? "" : shipType;
        Long pdAfter = Long.parseLong((after == null) ? "0" : after);
        Long pdBefore = (before == null) ? Long.MAX_VALUE : Long.parseLong(before);
        String used = (isUsed == null) ? "Any" : isUsed;
        Double mnSpeed = Double.parseDouble((minSpeed == null) ? "-1" : minSpeed);
        Double mxSpeed = (maxSpeed == null) ? Double.MAX_VALUE : Double.parseDouble(maxSpeed);
        Integer mnCrewSize = Integer.parseInt((minCrewSize == null) ? "0" : minCrewSize);
        Integer mxCrewSize = (maxCrewSize == null) ? Integer.MAX_VALUE : Integer.parseInt(maxCrewSize);
        Double mnRating = Double.parseDouble((minRating == null) ? "-1" : minRating);
        Double mxRating = (maxRating == null) ? Double.MAX_VALUE : Double.parseDouble(maxRating);
        Integer pNumber = Integer.parseInt((pageNumber == null) ? "0" : pageNumber);
        Integer pSize = Integer.parseInt((pageSize == null) ? "3" : pageSize);
        ShipOrder shipOrder = ShipOrder.valueOf((order == null) ? "ID" : order);

        Function<ShipOrder, Comparator<Ship>> orderComp = x -> {
            if (x == ShipOrder.SPEED) return Comparator.comparing(Ship::getSpeed);
            else if (x == ShipOrder.DATE) return Comparator.comparing(Ship::getProdDate);
            else if (x == ShipOrder.ID) return Comparator.comparing(Ship::getId);
            return Comparator.comparing(Ship::getRating);
        };

        BiFunction<String, Ship, Boolean> isMayBeused = (x, y) -> {
            if (x.equals("Any")) return true;
            return y.getUsed().equals(new Boolean(x));
        };

        if (allShips == null) allShips = (ArrayList<Ship>) shipRepository.findAll();

        ArrayList<Ship> filteredShips = allShips.stream()
                .filter(x -> x.getName().toLowerCase().contains(sName.toLowerCase()))
                .filter(x -> x.getPlanet().toLowerCase().contains(pName.toLowerCase()))
                .filter(x -> x.getShipType().toString().startsWith(sType))
                .filter(x -> x.getProdDate().getYear() > new Date(pdAfter).getYear())
                .filter(x -> x.getProdDate().getYear() < new Date(pdBefore).getYear())
                .filter(x -> isMayBeused.apply(used, x))
                .filter(x -> x.getSpeed() > mnSpeed)
                .filter(x -> x.getSpeed() < mxSpeed)
                .filter(x -> x.getCrewSize() > mnCrewSize)
                .filter(x -> x.getCrewSize() < mxCrewSize)
                .filter(x -> x.getRating() > mnRating)
                .filter(x -> x.getRating() < mxRating)
                .collect(Collectors.toCollection(ArrayList::new));

        count = filteredShips.size();

        ArrayList<Ship> toListShips = filteredShips.stream()
                .sorted(Comparator.comparing(Ship::getId))
                .skip(pNumber * pSize)
                .limit(pSize)
                .collect(Collectors.toCollection(ArrayList::new));

        return toListShips;
    }

    @GetMapping(path = "/rest/ships/count")
    public @ResponseBody
    long getCountShips() {
        return count;
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.POST)
    public @ResponseBody
    void addShipToDb(
            @RequestBody Ship ship
//            @PathVariable(name = "name") String name,
//            @PathVariable(name = "planet") String planet,
//            @PathVariable(name = "shipType") String shipType,
//            @PathVariable(name = "prodDate") Long prodDate,
//            @PathVariable(name = "isUsed", required=false) Boolean isUsed,
//            @PathVariable(name = "speed") Double speed,
//            @PathVariable(name = "crewSize") Integer crewSize
//            @RequestParam(name = "maxCrewSize", required = false) String maxCrewSize,
//            @RequestParam(name = "minRating", required = false) String minRating,
//            @RequestParam(name = "maxRating", required = false) String maxRating,
//            @RequestParam(name = "pageNumber", required = false) String pageNumber,
//            @RequestParam(name = "pageSize", required = false) String pageSize,
//            @RequestParam(name = "order", required = false) String order
    ) {
        System.out.println(ship);
        System.out.println(ship.getName());
        System.out.println(ship.getPlanet());
        System.out.println(ship.getShipType());
        System.out.println(ship.getProdDate());
        System.out.println(ship.getUsed());
        System.out.println(ship.getSpeed());
        System.out.println(ship.getCrewSize());
        System.out.println(ship.getRating());
        System.out.println("-----");
        Ship ship2 = new Ship("My","My",ShipType.MERCHANT,
                new Date(2995, 1,1),new Boolean(true),0.82,
                new Integer(617),1.31);
        Ship ship3 = new Ship();
        System.out.println("created");
        shipRepository.save(ship2);
        System.out.println("!!!!!!");


//        System.out.println(name);
//        System.out.println(planet);
//        System.out.println(shipType);
//        System.out.println(prodDate);
//        System.out.println(isUsed);
//        System.out.println(speed);
//        System.out.println(crewSize);

//        String sName = (name == null) ? "NoName" : name;
//        System.out.println(sName);
//        String pName = (planet == null) ? "NoPlanet" : planet;
//        System.out.println(pName);
//        String sType = (shipType == null) ? "Transport" : shipType;
//        System.out.println(sType);
//        Long prDate = (prodDate == null) ? "0" : prodDate;
//        System.out.println(prDate);
//        Boolean used = new Boolean(isUsed);
//        System.out.println(used);
//        Double spd = Double.parseDouble((speed == null) ? "0" : speed);
//        System.out.println(spd);
//        Integer cwSize = Integer.parseInt((crewSize == null) ? "0" : crewSize);
//        System.out.println(cwSize);

//        Double rating = 80 * speed * ((isUsed) ? 0.5 : 1.0) / (3019 - new Date(prodDate).getYear() + 1);
//        System.out.println(rating);
//
//        Ship newShip = new Ship(name, planet, ShipType.valueOf(shipType), new Date(prodDate),isUsed, speed, crewSize, rating);
//        System.out.println(newShip);
//        System.out.println();

//        shipRepository.save(newShip);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void deleteShipById(@PathVariable long id) {
        if (id < 0) throw new IllegalIdException();
        Ship ship = shipRepository.findById(id).orElseThrow(() -> new ShipNotFoundException());
        Predicate<Ship> sp = x -> x.getId().equals(new Long(id));
        allShips.removeIf(sp);
        shipRepository.delete(ship);
    }


}