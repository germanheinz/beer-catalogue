package com.haufe.beercatalogue.mapper;

import com.haufe.beercatalogue.domain.entity.Manufacturer;
import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    ManufacturerResponse toResponse(Manufacturer manufacturer);

    Manufacturer toEntity(ManufacturerRequest request);

    void updateEntity(ManufacturerRequest request, @MappingTarget Manufacturer manufacturer);
}
