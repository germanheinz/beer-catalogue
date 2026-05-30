package com.haufe.beercatalogue.service;

import com.haufe.beercatalogue.domain.entity.Beer;
import com.haufe.beercatalogue.domain.entity.Manufacturer;
import com.haufe.beercatalogue.domain.entity.User;
import com.haufe.beercatalogue.domain.enums.BeerType;
import com.haufe.beercatalogue.domain.enums.Role;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.exception.ForbiddenException;
import com.haufe.beercatalogue.exception.ResourceNotFoundException;
import com.haufe.beercatalogue.mapper.BeerMapper;
import com.haufe.beercatalogue.repository.BeerRepository;
import com.haufe.beercatalogue.repository.ManufacturerRepository;
import com.haufe.beercatalogue.repository.UserRepository;
import com.haufe.beercatalogue.service.impl.BeerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeerServiceTest {

    @Mock BeerRepository beerRepository;
    @Mock ManufacturerRepository manufacturerRepository;
    @Mock UserRepository userRepository;
    @Mock BeerMapper beerMapper;

    @InjectMocks BeerServiceImpl beerService;

    @Test
    void create_happyPath_returnsSavedBeer() {
        Manufacturer manufacturer = Manufacturer.builder().id(1L).name("Heineken").country("Netherlands").build();
        User adminUser = User.builder().id(1L).role(Role.ADMIN).build();
        BeerRequest request = new BeerRequest();
        request.setName("Heineken Lager");
        request.setAbv(new BigDecimal("5.0"));
        request.setType(BeerType.LAGER);
        request.setManufacturerId(1L);

        Beer beer = Beer.builder().id(1L).name("Heineken Lager").manufacturer(manufacturer).build();
        BeerResponse expected = new BeerResponse();
        expected.setId(1L);

        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(beerRepository.existsByNameAndManufacturerId("Heineken Lager", 1L)).thenReturn(false);
        when(beerMapper.toEntity(request)).thenReturn(beer);
        when(beerRepository.save(beer)).thenReturn(beer);
        when(beerMapper.toResponse(beer)).thenReturn(expected);

        BeerResponse result = beerService.create(request, 1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void create_manufacturerNotFound_throwsResourceNotFoundException() {
        BeerRequest request = new BeerRequest();
        request.setManufacturerId(99L);

        when(manufacturerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> beerService.create(request, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Manufacturer");
    }

    @Test
    void create_manufacturerUserEditingAnotherManufacturer_throwsForbiddenException() {
        Manufacturer heineken = Manufacturer.builder().id(1L).name("Heineken").build();
        Manufacturer guinness = Manufacturer.builder().id(2L).name("Guinness").build();
        User heinekenUser = User.builder().id(1L).role(Role.MANUFACTURER).manufacturer(heineken).build();

        BeerRequest request = new BeerRequest();
        request.setManufacturerId(2L);

        when(manufacturerRepository.findById(2L)).thenReturn(Optional.of(guinness));
        when(userRepository.findById(1L)).thenReturn(Optional.of(heinekenUser));

        assertThatThrownBy(() -> beerService.create(request, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("own manufacturer");
    }
}
