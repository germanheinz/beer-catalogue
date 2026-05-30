package com.haufe.beercatalogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haufe.beercatalogue.domain.enums.BeerType;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class BeerControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void getBeers_anonymous_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/beers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void createBeer_withoutAuth_returns401() throws Exception {
        BeerRequest request = buildBeerRequest();

        mockMvc.perform(post("/api/v1/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createBeer_asAdmin_returns201() throws Exception {
        BeerRequest request = buildBeerRequest();

        mockMvc.perform(post("/api/v1/beers")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test IPA"));
    }

    private BeerRequest buildBeerRequest() {
        BeerRequest request = new BeerRequest();
        request.setName("Test IPA");
        request.setAbv(new BigDecimal("6.5"));
        request.setType(BeerType.IPA);
        request.setDescription("A test IPA");
        request.setManufacturerId(1L);
        return request;
    }
}
