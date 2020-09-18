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
    @Query("SELECT s FROM Ship s WHERE " +
            "(s.shipType = :shipType1 OR s.shipType = :shipType2 OR s.shipType = :shipType3)" +
            "AND (s.isUsed = :isUsed1 OR s.isUsed = :isUsed2) AND " + //!!!
            "LOWER(s.name) LIKE LOWER(CONCAT('%',:name,'%'))" +
            "AND LOWER(s.planet) LIKE LOWER(CONCAT('%',:planet,'%'))" +
            "AND s.prodDate >= :after AND s.prodDate <= :before " +
            "AND s.speed >= :minSpeed AND s.speed <= :maxSpeed " +
            "AND s.crewSize >= :minCrewSize AND s.crewSize <= :maxCrewSize " +
            "AND s.rating >= :minRating AND s.rating <= :maxRating "
    )

    Page<Ship> searchAllByFilterParam(
            @Param("name") String name, @Param("planet") String planet,
            @Param("shipType1") ShipType shipType1,
            @Param("shipType2") ShipType shipType2,
            @Param("shipType3") ShipType shipType3,
            @Param("isUsed1") Boolean isUsed1,
            @Param("isUsed2") Boolean isUsed2,
            @Param("after") Date after, @Param("before") Date before,
            @Param("minSpeed") Double minSpeed, @Param("maxSpeed") Double maxSpeed,
            @Param("minCrewSize") Integer minCrewSize, @Param("maxCrewSize") Integer maxCrewSize,
            @Param("minRating") Double minRating, @Param("maxRating") Double maxRating,
            Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Ship s WHERE " +
            "(s.shipType = :shipType1 OR s.shipType = :shipType2 OR s.shipType = :shipType3)" +
            "AND (s.isUsed = :isUsed1 OR s.isUsed = :isUsed2) AND " + //!!!
            "LOWER(s.name) LIKE LOWER(CONCAT('%',:name,'%'))" +
            "AND LOWER(s.planet) LIKE LOWER(CONCAT('%',:planet,'%'))" +
            "AND s.prodDate >= :after AND s.prodDate <= :before " +
            "AND s.speed >= :minSpeed AND s.speed <= :maxSpeed " +
            "AND s.crewSize >= :minCrewSize AND s.crewSize <= :maxCrewSize " +
            "AND s.rating >= :minRating AND s.rating <= :maxRating "
    )

    Long countAllByFilterParam(
            @Param("name") String name, @Param("planet") String planet,
            @Param("shipType1") ShipType shipType1,
            @Param("shipType2") ShipType shipType2,
            @Param("shipType3") ShipType shipType3,
            @Param("isUsed1") Boolean isUsed1,
            @Param("isUsed2") Boolean isUsed2,
            @Param("after") Date after, @Param("before") Date before,
            @Param("minSpeed") Double minSpeed, @Param("maxSpeed") Double maxSpeed,
            @Param("minCrewSize") Integer minCrewSize, @Param("maxCrewSize") Integer maxCrewSize,
            @Param("minRating") Double minRating, @Param("maxRating") Double maxRating
    );
}



