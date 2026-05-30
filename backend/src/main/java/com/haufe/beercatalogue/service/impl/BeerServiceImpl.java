package com.haufe.beercatalogue.service.impl;

import com.haufe.beercatalogue.service.BeerService;
import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.domain.entity.Manufacturer;
import com.haufe.beercatalogue.domain.entity.User;
import com.haufe.beercatalogue.domain.enums.Role;
import com.haufe.beercatalogue.dto.request.BeerFilter;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.exception.DuplicateResourceException;
import com.haufe.beercatalogue.exception.ForbiddenException;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.mapper.BeerMapper;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import com.haufe.beercatalogue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final UserRepository userRepository;
    private final BeerMapper beerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BeerResponse> findAll(Pageable pageable) {
        return beerRepository.findAll(pageable).map(beerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BeerResponse> search(BeerFilter filter, Pageable pageable) {
        return beerRepository.search(filter.name(), filter.type(), filter.minAbv(), filter.maxAbv(), filter.manufacturerId(), pageable)
                .map(beerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BeerResponse findById(Long id) {
        return beerMapper.toResponse(findBeerById(id));
    }

    @Override
    @Transactional
    public BeerResponse create(BeerRequest request, Long currentUserId) {
        Manufacturer manufacturer = findManufacturerById(request.getManufacturerId());
        assertCanEditManufacturer(manufacturer, currentUserId);

        if (beerRepository.existsByNameAndManufacturerId(request.getName(), manufacturer.getId())) {
            throw new DuplicateResourceException("Beer already exists with name: " + request.getName());
        }

        Beer beer = beerMapper.toEntity(request);
        beer.setManufacturer(manufacturer);
        return beerMapper.toResponse(beerRepository.save(beer));
    }

    @Override
    @Transactional
    public BeerResponse update(Long id, BeerRequest request, Long currentUserId) {
        Beer beer = findBeerById(id);
        assertCanEditManufacturer(beer.getManufacturer(), currentUserId);

        Manufacturer newManufacturer = findManufacturerById(request.getManufacturerId());

        if (!beer.getName().equals(request.getName()) &&
                beerRepository.existsByNameAndManufacturerId(request.getName(), newManufacturer.getId())) {
            throw new DuplicateResourceException("Beer already exists with name: " + request.getName());
        }

        beerMapper.updateEntity(request, beer);
        beer.setManufacturer(newManufacturer);
        return beerMapper.toResponse(beerRepository.save(beer));
    }

    @Override
    @Transactional
    public void delete(Long id, Long currentUserId) {
        Beer beer = findBeerById(id);
        assertCanEditManufacturer(beer.getManufacturer(), currentUserId);
        beerRepository.delete(beer);
    }

    private Beer findBeerById(Long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beer", id));
    }

    private Manufacturer findManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer", id));
    }

    private void assertCanEditManufacturer(Manufacturer manufacturer, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

        if (user.getRole() == Role.ADMIN) return;

        boolean ownsManufacturer = user.getManufacturer() != null &&
                user.getManufacturer().getId().equals(manufacturer.getId());

        if (!ownsManufacturer) {
            throw new ForbiddenException("You can only edit beers from your own manufacturer");
        }
    }
}
