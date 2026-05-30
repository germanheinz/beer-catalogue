package com.haufe.beercatalogue.repository;

import com.haufe.beercatalogue.domain.entity.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    Optional<Manufacturer> findByName(String name);

    boolean existsByName(String name);

    Page<Manufacturer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
