package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ShipService {
    Ship save(Ship ship);

    List<Ship> getShipsByFilterParam(
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
    );

    long getShipCount();

    Ship getById(Long id);

    Ship updateShipById(Long id, Ship sample);

    void deleteById(Long id);

    Ship addNewShip(Ship sample);
}
