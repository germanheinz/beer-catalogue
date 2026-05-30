package com.haufe.beercatalogue.repository;

import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.domain.enums.BeerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BeerRepository extends JpaRepository<Beer, Long>, JpaSpecificationExecutor<Beer> {

    Page<Beer> findByManufacturerId(Long manufacturerId, Pageable pageable);

    boolean existsByNameAndManufacturerId(String name, Long manufacturerId);
}
