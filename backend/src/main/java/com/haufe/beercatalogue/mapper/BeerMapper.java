package com.haufe.beercatalogue.mapper;

import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ManufacturerMapper.class})
public interface BeerMapper {

    BeerResponse toResponse(Beer beer);

    @Mapping(target = "manufacturer", ignore = true)
    Beer toEntity(BeerRequest request);

    @Mapping(target = "manufacturer", ignore = true)
    void updateEntity(BeerRequest request, @MappingTarget Beer beer);
}
