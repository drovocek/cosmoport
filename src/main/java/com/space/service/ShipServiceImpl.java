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

@Service("shipService")
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    //Если корабль не найден в БД, отвечает ошибкой с кодом 404.
    public Ship getById(String id) {
        Long idAsLong = null;
        if (isExistById(id)) idAsLong = Long.parseLong(id);
        return shipRepository.findById(idAsLong).orElseThrow(() -> new ShipNotFoundException());
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

        Pageable limit = PageRequest.of(Integer.parseInt(pageNumber)
                , Integer.parseInt(pageSize)
                , Sort.by(ShipOrder.valueOf(order).getFieldName()));

        ShipType[] types = getShipTypeArrForQuery(shipType);
        Boolean[] used = getBooleanArrForQuery(isUsed);

        return shipRepository.searchAllByFilterParam(
                name, planet,
                types[0], types[1], types[2],
                used[0], used[1],
                new Date(Long.parseLong(after)), new Date(Long.parseLong(before)),
                Double.parseDouble(minSpeed), Double.parseDouble(maxSpeed),
                Integer.parseInt(minCrewSize), Integer.parseInt(maxCrewSize),
                Double.parseDouble(minRating), Double.parseDouble(maxRating),
                limit
        );
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
        ShipType[] types = getShipTypeArrForQuery(shipType);
        Boolean[] used = getBooleanArrForQuery(isUsed);

        Long shipCount = shipRepository.countAllByFilterParam(
                name, planet,
                types[0], types[1], types[2],
                used[0], used[1],
                new Date(Long.parseLong(after)), new Date(Long.parseLong(before)),
                Double.parseDouble(minSpeed), Double.parseDouble(maxSpeed),
                Integer.parseInt(minCrewSize), Integer.parseInt(maxCrewSize),
                Double.parseDouble(minRating), Double.parseDouble(maxRating)
        );

        return shipCount;
    }

    //utills
    //калькуляция рейтинга корабля
    private Double calculateShipRating(Ship ship) {
        Double a = 80 * ship.getSpeed() * ((ship.getIsUsed() == true) ? 0.5 : 1);
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
    private boolean isExistById(String id) {
        boolean isValid = true;
        Long checker;
        try {
            checker = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgException();
        }

        if (checker <= 0) throw new IllegalArgException();

        return isValid;
    }

    //округляет Double до dec нулей после запятой
    private Double roundDecDouble(Double num, int dec) {
        BigDecimal bd = new BigDecimal(Double.toString(num));
        bd = bd.setScale(dec, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Обновлять нужно только те поля, которые не null.
    //3. При обновлении или создании корабля игнорируем параметры “id” и “rating” из тела запроса
    private void validateShipParamAndSetIfNotNull(Ship supplier, Ship consumer, boolean nullIsValidParam) {
        String name = supplier.getName();
        String planet = supplier.getPlanet();
        ShipType shipType = supplier.getShipType();
        Boolean isUsed = supplier.getIsUsed();
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

        if (!nullIsValidParam) consumer.setIsUsed((isUsed != null) ? isUsed : false);

        consumer.setRating(
                calculateShipRating(consumer));
    }

    private ShipType[] getShipTypeArrForQuery(String request) {
        return (request.equals("Any")) ? ShipType.values() :
                new ShipType[]{
                        ShipType.valueOf(request),
                        ShipType.valueOf(request),
                        ShipType.valueOf(request)
                };
    }
    private Boolean[] getBooleanArrForQuery(String request) {
        return (request.equals("Any")) ?
                new Boolean[]{
                        new Boolean(true),
                        new Boolean(false)
                }
                :
                new Boolean[]{
                        new Boolean(request),
                        new Boolean(request)
                };
    }

}
