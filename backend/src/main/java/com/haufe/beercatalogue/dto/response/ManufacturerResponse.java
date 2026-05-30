package com.haufe.beercatalogue.dto.response;

import lombok.Data;

@Data
public class ManufacturerResponse {

    private Long id;
    private String name;
    private String country;
}
