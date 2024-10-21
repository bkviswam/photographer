package com.intuit.craft.photographer.repository;

import com.intuit.craft.photographer.model.Photographer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotographerRepository extends JpaRepository<Photographer, Integer> {

    Page<Photographer> findAll(Pageable pageable);

    @Query("SELECT p FROM Photographer p JOIN p.eventType e WHERE :eventType IN (e)")
    List<Photographer> findByEventType(@Param("eventType") String eventType);

    @Query(value = """
            SELECT *,\s
            (6371 * acos(cos(radians(:lat)) * cos(radians(latitude))\s
            * cos(radians(longitude) - radians(:lng))\s
            + sin(radians(:lat)) * sin(radians(latitude)))) AS distance\s
            FROM photographers\s
            HAVING distance < :radius\s
            ORDER BY distance ASC
           \s""", nativeQuery = true)
    List<Photographer> findPhotographersByProximity(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radius);
}
