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

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


@Controller
public class ShipController {
    @Autowired
    private ShipRepository shipRepository;
    private long count;

    @GetMapping(path = "/rest/ships/count")
    public @ResponseBody
    long getCountShips() {
        return count;
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public String deleteShipById(@PathVariable long id) {
        if (id <= 0) throw new IllegalIdException();
        Ship ship = shipRepository.findById(id).orElseThrow(() -> new ShipNotFoundException());
        shipRepository.delete(ship);
        return "redirect:/rest/ships";
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public @ResponseBody
    Iterable<Ship> getAllShips(
            /*String name,?name=Orion III*/ @RequestParam(name = "name", required = false) String name,
            /*String planet,&planet=Mars*/ @RequestParam(name = "planet", required = false) String planet,
            /*ShipType shipType,&shipType=MERCHANT*/ @RequestParam(name = "shipType", required = false) String shipType,
            /*Long after,&after=NaN*/ @RequestParam(name = "after", required = false) String after,
            /*Long before,&before=NaN*/ @RequestParam(name = "before", required = false) String before,
            /*Boolean isUsed,???*/ @RequestParam(name = "isUsed", required = false) String isUsed,
            /*Double minSpeed,&minSpeed*/ @RequestParam(name = "minSpeed", required = false) String minSpeed,
            /*Double maxSpeed,&maxSpeed=0.83*/ @RequestParam(name = "maxSpeed", required = false) String maxSpeed,
            /*Integer minCrewSize,*/ @RequestParam(name = "minCrewSize", required = false) String minCrewSize,
            /*Integer maxCrewSize,*/ @RequestParam(name = "maxCrewSize", required = false) String maxCrewSize,
            /*Double minRating,&minCrewSize=616*/ @RequestParam(name = "minRating", required = false) String minRating,
            /*Double maxRating,&maxCrewSize=618*/ @RequestParam(name = "maxRating", required = false) String maxRating,
            /*Integer pageNumber, &pageNumber=0*/ @RequestParam(name = "pageNumber", required = false) String pageNumber,
            /*Integer pageSize&pageSize*/ @RequestParam(name = "pageSize", required = false) String pageSize,
            /*ShipOrder order, &order=ID*/ @RequestParam(name = "order", required = false) String order
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

        ArrayList<Ship> allShips = (ArrayList<Ship>) shipRepository.findAll();

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

        ArrayList<Ship> responce = filteredShips.stream()
                .sorted(orderComp.apply(shipOrder))
                .skip(pNumber * pSize)
                .limit(pSize)
                .collect(Collectors.toCollection(ArrayList::new));

        return responce;
    }

//    @RequestMapping(path = "/rest/ships/filter", method = RequestMethod.GET)
//    public @ResponseBody
//    Iterable<Ship> getAllShips(
//
//    ){
//
//    }
}