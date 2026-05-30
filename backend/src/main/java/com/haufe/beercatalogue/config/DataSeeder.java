package com.haufe.beercatalogue.config;

import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.domain.entity.Manufacturer;
import com.haufe.beercatalogue.domain.entity.User;
import com.haufe.beercatalogue.domain.enums.BeerType;
import com.haufe.beercatalogue.domain.enums.Role;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import com.haufe.beercatalogue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ManufacturerRepository manufacturerRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        Manufacturer heineken = manufacturerRepository.save(
                Manufacturer.builder().name("Heineken").country("Netherlands").build());

        Manufacturer guinness = manufacturerRepository.save(
                Manufacturer.builder().name("Guinness").country("Ireland").build());

        Manufacturer paulaner = manufacturerRepository.save(
                Manufacturer.builder().name("Paulaner").country("Germany").build());

        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .username("heineken_user")
                .password(passwordEncoder.encode("heineken123"))
                .role(Role.MANUFACTURER)
                .manufacturer(heineken)
                .build());

        userRepository.save(User.builder()
                .username("guinness_user")
                .password(passwordEncoder.encode("guinness123"))
                .role(Role.MANUFACTURER)
                .manufacturer(guinness)
                .build());

        beerRepository.save(Beer.builder()
                .name("Heineken Lager")
                .abv(new BigDecimal("5.0"))
                .type(BeerType.LAGER)
                .description("A refreshing Dutch lager with a crisp, clean taste.")
                .manufacturer(heineken)
                .build());

        beerRepository.save(Beer.builder()
                .name("Heineken Silver")
                .abv(new BigDecimal("4.0"))
                .type(BeerType.LAGER)
                .description("A lighter, smoother version of the classic Heineken.")
                .manufacturer(heineken)
                .build());

        beerRepository.save(Beer.builder()
                .name("Guinness Draught")
                .abv(new BigDecimal("4.2"))
                .type(BeerType.STOUT)
                .description("The iconic Irish stout with a creamy head and roasted flavor.")
                .manufacturer(guinness)
                .build());

        beerRepository.save(Beer.builder()
                .name("Guinness Extra Stout")
                .abv(new BigDecimal("5.6"))
                .type(BeerType.STOUT)
                .description("A bolder, more robust stout with intense roasted malt character.")
                .manufacturer(guinness)
                .build());

        beerRepository.save(Beer.builder()
                .name("Paulaner Hefe-Weizen")
                .abv(new BigDecimal("5.5"))
                .type(BeerType.WHEAT)
                .description("A classic Bavarian wheat beer with banana and clove notes.")
                .manufacturer(paulaner)
                .build());
    }
}
