package com.haufe.beercatalogue.repository;

import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.domain.enums.BeerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    @Query("SELECT b FROM Beer b WHERE " +
           "(:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR b.type = :type) AND " +
           "(:minAbv IS NULL OR b.abv >= :minAbv) AND " +
           "(:maxAbv IS NULL OR b.abv <= :maxAbv) AND " +
           "(:manufacturerId IS NULL OR b.manufacturer.id = :manufacturerId)")
    Page<Beer> search(
            @Param("name") String name,
            @Param("type") BeerType type,
            @Param("minAbv") BigDecimal minAbv,
            @Param("maxAbv") BigDecimal maxAbv,
            @Param("manufacturerId") Long manufacturerId,
            Pageable pageable);

    boolean existsByNameAndManufacturerId(String name, Long manufacturerId);
}
