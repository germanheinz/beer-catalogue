package com.haufe.beercatalogue.dto.request;

import com.haufe.beercatalogue.domain.enums.BeerType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "ABV is required")
    @DecimalMin(value = "0.0", message = "ABV must be >= 0.0")
    @DecimalMax(value = "99.9", message = "ABV must be <= 99.9")
    private BigDecimal abv;

    @NotNull(message = "Type is required")
    private BeerType type;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Manufacturer ID is required")
    private Long manufacturerId;
}
