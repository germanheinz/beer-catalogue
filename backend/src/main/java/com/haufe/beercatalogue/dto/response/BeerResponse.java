package com.haufe.beercatalogue.dto.response;

import com.haufe.beercatalogue.domain.enums.BeerType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeerResponse {

    private Long id;
    private String name;
    private BigDecimal abv;
    private BeerType type;
    private String description;
    private ManufacturerResponse manufacturer;
}
