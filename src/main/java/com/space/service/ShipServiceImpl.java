package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.model.errors.IllegalIdException;
import com.space.model.errors.ShipNotFoundException;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service("shipService")
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShipRepository shipRepository;
    private long shipCount;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        //TODO добавить метод проверки валидности id if (clientRepository.existsById(id))
        return shipRepository.findById(id).orElseThrow(() -> new ShipNotFoundException());
    }

    @Override
    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public void deleteById(Long id) {
        shipRepository.delete(getById(id));
    }

    @Override
    public Ship updateShipById(Long id, Ship sample) {
        Ship ship = getById(id);

        String name = sample.getName();
        String planet = sample.getPlanet();
        ShipType shipType = sample.getShipType();
        Date prodDate = sample.getProdDate();
        Boolean isUsed = sample.getUsed();
        Double speed = sample.getSpeed();
        Integer crewSize = sample.getCrewSize();
        Double rating = sample.getRating();

        //Обновлять нужно только те поля, которые не null.
        if (name != null) ship.setName(name);
        if (planet != null) ship.setPlanet(planet);
        if (shipType != null) ship.setShipType(shipType);
        if (prodDate != null) ship.setProdDate(prodDate);
        if (isUsed != null) ship.setUsed(isUsed);
        if (speed != null) ship.setSpeed(speed);
        if (crewSize != null) ship.setCrewSize(crewSize);

        /*3. При обновлении или создании корабля игнорируем
        параметры “id” и “rating” из тела запроса*/
        ship.setRating(
                calculateShipRating(
                        ship.getSpeed(),
                        ship.getUsed(),
                        ship.getProdDate().toLocalDate()));

        return shipRepository.save(ship);
    }

    @Override
    public Ship addNewShip(Ship sample) {
        System.out.println(sample);
        Ship ship = new Ship();

        String name = sample.getName();
        String planet = sample.getPlanet();
        ShipType shipType = sample.getShipType();
        Date prodDate = sample.getProdDate();
        Boolean isUsed = sample.getUsed();
        Double speed = sample.getSpeed();
        Integer crewSize = sample.getCrewSize();

        if (name == null || planet == null || shipType == null
                || prodDate == null || speed == null
                || crewSize == null) throw new IllegalIdException();
        else {
            ship.setName(name);
            ship.setPlanet(planet);
            ship.setShipType(shipType);
            ship.setProdDate(prodDate);
            ship.setSpeed(speed);
            ship.setCrewSize(crewSize);
        }
        /*1. Если в запросе на создание корабля нет параметра “isUsed”,
         то считаем, что пришло значение “false”.*/
        ship.setUsed((isUsed != null) ? isUsed : false);

        /*3. При обновлении или создании корабля игнорируем
        параметры “id” и “rating” из тела запроса*/
        ship.setRating(calculateShipRating(
                ship.getSpeed(),
                ship.getUsed(),
                ship.getProdDate().toLocalDate()));

        return shipRepository.save(ship);
    }

    public List<Ship> getShipsByFilterParam(
            String name, String planet,
            String shipType,
            String after, String before,
            String isUsed,
            String minSpeed, String maxSpeed,
            String minCrewSize, String maxCrewSize,
            String minRating, String maxRating,
            String pageNumber,
            String pageSize,
            String order
    ) {
        System.out.println("!!!!!getShipsByFilterParam!!!!");
        String sName = (name == null) ? "" : name;
        String pName = (planet == null) ? "" : planet;
        String sType = (shipType == null) ? "Any" : shipType;
        Long pdAfter = Long.parseLong((after == null) ? "0" : after);
        Long pdBefore = (before == null) ? new Long(93095643600000L) : Long.parseLong(before);
        String used = (isUsed == null) ? "Any" : isUsed;
        Double mnSpeed = Double.parseDouble((minSpeed == null) ? "-1" : minSpeed);
        Double mxSpeed = (maxSpeed == null) ? Double.MAX_VALUE : Double.parseDouble(maxSpeed);
        Integer mnCrewSize = Integer.parseInt((minCrewSize == null) ? "0" : minCrewSize);
        Integer mxCrewSize = (maxCrewSize == null) ? Integer.MAX_VALUE : Integer.parseInt(maxCrewSize);
        Double mnRating = Double.parseDouble((minRating == null) ? "-1" : minRating);
        Double mxRating = (maxRating == null) ? Double.MAX_VALUE : Double.parseDouble(maxRating);

//        4. Если параметр pageNumber не указан – нужно использовать значение 0
        Integer pNumber = Integer.parseInt((pageNumber == null) ? "0" : pageNumber);

//        5. Если параметр pageSize не указан – нужно использовать значение 3. 6
        Integer pSize = Integer.parseInt((pageSize == null) ? "3" : pageSize);
        ShipOrder shipOrder = ShipOrder.valueOf((order == null) ? "ID" : order);

        Function<String, Predicate<Ship>> getIsUsedPred = x -> {
            if (x.equals("Any")) return y -> true;
            return y -> y.getUsed().equals(new Boolean(x));
        };

        ShipType[] st = (sType.equals("Any")) ?
                new ShipType[]{ShipType.MILITARY, ShipType.MERCHANT, ShipType.TRANSPORT}
                : new ShipType[]{ShipType.valueOf(sType), ShipType.valueOf(sType), ShipType.valueOf(sType)};

        Pageable limit = PageRequest.of(pNumber, pSize, Sort.by(shipOrder.getFieldName()));
        Page<Ship> result = (sType.equals("Any")) ?
                shipRepository.searchAll(
                        sName,
                        pName,
                        new Date(pdAfter), new Date(pdBefore),
                        //    new Boolean(true),
                        mnSpeed, mxSpeed,
                        mnCrewSize, mxCrewSize,
                        mnRating, mxRating,
                        limit
                )
                :
                shipRepository.searchAllByType(
                        sName,
                        pName,
                        ShipType.valueOf(sType),
                        new Date(pdAfter), new Date(pdBefore),
                        //    new Boolean(true),
                        mnSpeed, mxSpeed,
                        mnCrewSize, mxCrewSize,
                        mnRating, mxRating,
                        limit
                );

        shipCount = result.getTotalElements();

        return result.getContent();//.stream().filter(getIsUsedPred.apply(used)).collect(Collectors.toList());
    }

    public long getShipCount() {
        return shipCount;
    }

    private Double calculateShipRating(Double speed, Boolean isUsed, LocalDate prodDate) {
        Double a = 80 * speed * ((isUsed == true) ? 0.5 : 1);
        Integer b = (LocalDate.of(3019, 1, 1).getYear() - prodDate.getYear() + 1);
        Double rating = a / b;
        System.out.println(a);
        System.out.println(b);

        BigDecimal bd = new BigDecimal(Double.toString(rating));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    /*Не валидным считается id, если он:
      - не числовой
      - не целое число
      - не положительный*/

//Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404.
//    Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
    private boolean existsById(Object id){
        boolean isValid = true;

        return isValid;
    }
}
