package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManufacturerService {

    Page<ManufacturerResponse> findAll(String name, Pageable pageable);

    ManufacturerResponse findById(Long id);

    ManufacturerResponse create(ManufacturerRequest request);

    ManufacturerResponse update(Long id, ManufacturerRequest request, Long currentUserId);

    void delete(Long id, Long currentUserId);
}
