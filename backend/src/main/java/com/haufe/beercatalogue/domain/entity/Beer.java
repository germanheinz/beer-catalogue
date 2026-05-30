package com.haufe.beercatalogue.domain.entity;

import com.haufe.beercatalogue.domain.enums.BeerType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "beers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal abv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BeerType type;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;
}
