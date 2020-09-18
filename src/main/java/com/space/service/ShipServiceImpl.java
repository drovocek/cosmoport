package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.model.exceptions.IllegalArgException;
import com.space.model.exceptions.ShipNotFoundException;
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
    //Если корабль не найден в БД, отвечает ошибкой с кодом 404.
    public Ship getById(String id) {
        Long idLong = null;
        if (existsById(id)) idLong = Long.parseLong(id);
        return shipRepository.findById(idLong).orElseThrow(() -> new ShipNotFoundException());
    }

    @Override
    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public void deleteById(String id) {
        shipRepository.delete(getById(id));
    }

    @Override
    public Ship updateShipById(String id, Ship supplier) {
        Ship consumer = getById(id);
        validateShipParamAndSetIfNotNull(supplier, consumer, true);
        return shipRepository.save(consumer);
    }

    @Override
    public Ship addNewShip(Ship sample) {
        Ship ship = new Ship();
        validateShipParamAndSetIfNotNull(sample, ship, false);
        return shipRepository.save(ship);
    }

    public Page<Ship> getShipsByFilterParam(
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
        Date pdAfter = new Date(Long.parseLong((after == null) ? "26160710400000" : after)); //2799
        Date pdBefore = new Date(Long.parseLong((before == null) ? "33134745600000" : before)); //3020
        String used = (isUsed == null) ? "Any" : isUsed;
        Double mnSpeed = Double.parseDouble((minSpeed == null) ? "0" : minSpeed);
        Double mxSpeed = Double.parseDouble((maxSpeed == null) ? "1" : maxSpeed);
        Integer mnCrewSize = Integer.parseInt((minCrewSize == null) ? "0" : minCrewSize);
        Integer mxCrewSize = Integer.parseInt((maxCrewSize == null) ? "10000" : maxCrewSize);
        Double mnRating = Double.parseDouble((minRating == null) ? "-1" : minRating);
        Double mxRating = Double.parseDouble((maxRating == null) ? "100" : maxRating);

        System.out.println("sName: " + sName);
        System.out.println("pName: " + pName);
        System.out.println("sType: " + sType);
        System.out.println("pdAfter: " + pdAfter);
        System.out.println("pdBefore: " + pdBefore);
        System.out.println("used: " + used);
        System.out.println("mnSpeed: " + mnSpeed);
        System.out.println("mxSpeed: " + mxSpeed);
        System.out.println("mnCrewSize: " + mnCrewSize);
        System.out.println("mxCrewSize: " + mxCrewSize);
        System.out.println("mnRating: " + mnRating);
        System.out.println("mxRating: " + mxRating);

//        4. Если параметр pageNumber не указан – нужно использовать значение 0
        Integer pNumber = Integer.parseInt((pageNumber == null) ? "0" : pageNumber);

//        5. Если параметр pageSize не указан – нужно использовать значение 3. 6
        Integer pSize = Integer.parseInt((pageSize == null) ? "3" : pageSize);
        ShipOrder shipOrder = ShipOrder.valueOf((order == null) ? "ID" : order);

        Pageable limit = PageRequest.of(pNumber, pSize, Sort.by(shipOrder.getFieldName()));
        Page<Ship> result = null;

        if(sType.equals("Any") && used.equals("Any")){
            result = shipRepository.searchAll(
                    sName, pName,
                    pdAfter, pdBefore,
                    mnSpeed, mxSpeed,
                    mnCrewSize, mxCrewSize,
                    mnRating, mxRating,
                    limit
            );
        }
//        else if(sType.equals("Any")){
//            result = shipRepository.searchAllByBool(
//                    sName, pName,
//                    new Boolean(used),
//                    pdAfter, pdBefore,
//                    mnSpeed, mxSpeed,
//                    mnCrewSize, mxCrewSize,
//                    mnRating, mxRating,
//                    limit
//            );
//        }
//        else if(used.equals("Any")){
//            result = shipRepository.searchAllByType(
//                    sName, pName,
//                    ShipType.valueOf(sType),
//                    pdAfter, pdBefore,
//                    mnSpeed, mxSpeed,
//                    mnCrewSize, mxCrewSize,
//                    mnRating, mxRating,
//                    limit
//            );
//        }
//        else{
//            result = shipRepository.searchAllByTypeAndBool(
//                    sName, pName,
//                    ShipType.valueOf(sType),
//                    new Boolean(used),
//                    pdAfter, pdBefore,
//                    mnSpeed, mxSpeed,
//                    mnCrewSize, mxCrewSize,
//                    mnRating, mxRating,
//                    limit
//            );
//        }

        shipCount = result.getTotalElements();

        return result;
    }

    public Long getShipCount(
            String name, String planet,
            String shipType,
            String after, String before,
            String isUsed,
            String minSpeed, String maxSpeed,
            String minCrewSize, String maxCrewSize,
            String minRating, String maxRating
    ) {
        Long count = getShipsByFilterParam(
                name, planet,
                shipType,
                after, before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,
                null,
                null,
                null
        ).getTotalElements();

        System.out.println("----------------");
        System.out.println("count: " + count);
        System.out.println("----------------");
        System.out.println("----------------");

        return count;
    }

    //калькуляция рейтинга корабля
    private Double calculateShipRating(Ship ship) {
        Double a = 80 * ship.getSpeed() * ((ship.getUsed() == true) ? 0.5 : 1);
        Integer b = (LocalDate.of(3019, 1, 1).getYear() - ship.getProdDate().toLocalDate().getYear() + 1);
        Double rating = a / b;
        return roundDecDouble(rating, 2);

    }

    /*Проверяет валидность id
    Не валидным считается id, если он:
      - не числовой
      - не целое число
      - не положительный
    Если значение id не валидное, отвечает ошибкой с кодом 400.*/
    private boolean existsById(String id) {
        boolean isValid = true;
        Long l;
        try {
            l = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgException();
        }

        if (l <= 0) throw new IllegalArgException();

        return isValid;
    }

    //округляет Double до dec нулей после запятой
    private Double roundDecDouble(Double num, int dec) {
        BigDecimal bd = new BigDecimal(Double.toString(num));
        bd = bd.setScale(dec, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //utills
    //Обновлять нужно только те поля, которые не null.
    //3. При обновлении или создании корабля игнорируем параметры “id” и “rating” из тела запроса
    private void validateShipParamAndSetIfNotNull(Ship supplier, Ship consumer, boolean nullIsValidParam) {
        String name = supplier.getName();
        String planet = supplier.getPlanet();
        ShipType shipType = supplier.getShipType();
        Boolean isUsed = supplier.getUsed();
        Date prodDate = supplier.getProdDate();
        Double speed = supplier.getSpeed();
        Integer crewSize = supplier.getCrewSize();

        if (!nullIsValidParam) {
            if (name == null
                    || planet == null
                    || shipType == null
                    || prodDate == null
                    || speed == null
                    || crewSize == null
            ) throw new IllegalArgException();
        }

        if (name != null) {
            if (name.length() > 50 || name.isEmpty()) throw new IllegalArgException();
            else consumer.setName(name);
        }

        if (planet != null) {
            if (planet.length() > 50 || planet.isEmpty()) throw new IllegalArgException();
            else consumer.setPlanet(planet);
        }

        if (shipType != null) consumer.setShipType(shipType);

        if (prodDate != null) {
            if (prodDate.getTime() < 0 || prodDate.toLocalDate().getYear() < 2800 || prodDate.toLocalDate().getYear() >= 3019)
                throw new IllegalArgException();
            else consumer.setProdDate(prodDate);
        }

        if (speed != null) {
            if (speed < 0.01 || speed >= 0.99) throw new IllegalArgException();
            else consumer.setSpeed(roundDecDouble(speed, 2));
        }
        if (crewSize != null) {
            if (crewSize < 1 || crewSize >= 9999) throw new IllegalArgException();
            else consumer.setCrewSize(crewSize);
        }

        if (!nullIsValidParam) consumer.setUsed((isUsed != null) ? isUsed : false);

        consumer.setRating(
                calculateShipRating(consumer));
    }
}
