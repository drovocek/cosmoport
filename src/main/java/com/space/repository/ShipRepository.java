package com.space.repository;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

//@Transactional
@Repository("shipRepository")
public interface ShipRepository extends JpaRepository<Ship, Long> {
    //TODO добавить поиск по isUsed
    //TODO При передаче границ диапазонов
    // (параметры с именами, которые начинаются на «min» или «max»)
    // границы нужно использовать включительно.
    @Query("SELECT s FROM Ship s WHERE " +
            "s.shipType = :shipType " +
            //   "AND s.used = :used " + //!!!
            "AND LOWER(s.name) LIKE LOWER(CONCAT('%',:name,'%'))" +
            "AND LOWER(s.planet) LIKE LOWER(CONCAT('%',:planet,'%'))" +
            "AND s.prodDate BETWEEN :after AND :before " +
            "AND s.speed BETWEEN :minSpeed AND :maxSpeed " +
            "AND s.crewSize BETWEEN :minCrewSize AND :maxCrewSize " +
            "AND s.rating BETWEEN :minRating AND :maxRating "


    )
    Page<Ship> searchAllByType(
            @Param("name") String name, @Param("planet") String planet,
            @Param("shipType") ShipType shipType,
            @Param("after") Date after, @Param("before") Date before,
            // @Param("used") Boolean used, //!!!
            @Param("minSpeed") Double minSpeed, @Param("maxSpeed") Double maxSpeed,
            @Param("minCrewSize") Integer minCrewSize, @Param("maxCrewSize") Integer maxCrewSize,
            @Param("minRating") Double minRating, @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    //TODO добавить поиск по isUsed
    @Query("SELECT s FROM Ship s WHERE " +
            //   "s.used = :used " + //!!!
            "LOWER(s.name) LIKE LOWER(CONCAT('%',:name,'%'))" +
            "AND LOWER(s.planet) LIKE LOWER(CONCAT('%',:planet,'%'))" +
            "AND s.prodDate BETWEEN :after AND :before " +
            "AND s.speed BETWEEN :minSpeed AND :maxSpeed " +
            "AND s.crewSize BETWEEN :minCrewSize AND :maxCrewSize " +
            "AND s.rating BETWEEN :minRating AND :maxRating "


    )
    Page<Ship> searchAll(
            @Param("name") String name, @Param("planet") String planet,
            @Param("after") Date after, @Param("before") Date before,
            // @Param("used") Boolean used, //!!!
            @Param("minSpeed") Double minSpeed, @Param("maxSpeed") Double maxSpeed,
            @Param("minCrewSize") Integer minCrewSize, @Param("maxCrewSize") Integer maxCrewSize,
            @Param("minRating") Double minRating, @Param("maxRating") Double maxRating,
            Pageable pageable
    );
}



