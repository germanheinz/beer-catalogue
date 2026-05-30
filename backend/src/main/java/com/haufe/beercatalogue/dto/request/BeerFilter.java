package com.haufe.beercatalogue.dto.request;

import com.haufe.beercatalogue.domain.enums.BeerType;

import java.math.BigDecimal;

public record BeerFilter(
        String name,
        BeerType type,
        BigDecimal minAbv,
        BigDecimal maxAbv,
        Long manufacturerId
) {}
