package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.dto.request.BeerFilter;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BeerService {

    Page<BeerResponse> findAll(Pageable pageable);

    Page<BeerResponse> search(BeerFilter filter, Pageable pageable);

    BeerResponse findById(Long id);

    BeerResponse create(BeerRequest request, Long currentUserId);

    BeerResponse update(Long id, BeerRequest request, Long currentUserId);

    void delete(Long id, Long currentUserId);
}
