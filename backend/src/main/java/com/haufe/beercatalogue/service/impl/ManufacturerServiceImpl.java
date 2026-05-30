package com.haufe.beercatalogue.service.impl;

import com.haufe.beercatalogue.service.ManufacturerService;
import com.haufe.beercatalogue.domain.entity.Manufacturer;
import com.haufe.beercatalogue.domain.entity.User;
import com.haufe.beercatalogue.domain.enums.Role;
import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import com.haufe.beercatalogue.exception.DuplicateResourceException;
import com.haufe.beercatalogue.exception.ForbiddenException;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.mapper.ManufacturerMapper;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import com.haufe.beercatalogue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final UserRepository userRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ManufacturerResponse> findAll(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return manufacturerRepository.findByNameContainingIgnoreCase(name, pageable)
                    .map(manufacturerMapper::toResponse);
        }
        return manufacturerRepository.findAll(pageable).map(manufacturerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerResponse findById(Long id) {
        return manufacturerMapper.toResponse(findManufacturerById(id));
    }

    @Override
    @Transactional
    public ManufacturerResponse create(ManufacturerRequest request) {
        if (manufacturerRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Manufacturer already exists with name: " + request.getName());
        }
        Manufacturer manufacturer = manufacturerMapper.toEntity(request);
        return manufacturerMapper.toResponse(manufacturerRepository.save(manufacturer));
    }

    @Override
    @Transactional
    public ManufacturerResponse update(Long id, ManufacturerRequest request, Long currentUserId) {
        Manufacturer manufacturer = findManufacturerById(id);
        assertCanEdit(manufacturer, currentUserId);

        if (!manufacturer.getName().equals(request.getName()) && manufacturerRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Manufacturer already exists with name: " + request.getName());
        }

        manufacturerMapper.updateEntity(request, manufacturer);
        return manufacturerMapper.toResponse(manufacturerRepository.save(manufacturer));
    }

    @Override
    @Transactional
    public void delete(Long id, Long currentUserId) {
        Manufacturer manufacturer = findManufacturerById(id);
        assertCanEdit(manufacturer, currentUserId);
        manufacturerRepository.delete(manufacturer);
    }

    private Manufacturer findManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer", id));
    }

    private void assertCanEdit(Manufacturer manufacturer, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));

        if (user.getRole() == Role.ADMIN) return;

        boolean ownsManufacturer = user.getManufacturer() != null &&
                user.getManufacturer().getId().equals(manufacturer.getId());

        if (!ownsManufacturer) {
            throw new ForbiddenException("You can only edit your own manufacturer");
        }
    }
}
