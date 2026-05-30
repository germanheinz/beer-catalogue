package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.domain.enums.BeerType;
import com.haufe.beercatalogue.dto.request.BeerFilter;
import com.haufe.beercatalogue.dto.request.BeerRequest;
import com.haufe.beercatalogue.dto.response.BeerResponse;
import com.haufe.beercatalogue.security.UserPrincipal;
import com.haufe.beercatalogue.service.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/beers")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;

    @GetMapping
    public ResponseEntity<Page<BeerResponse>> findAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(beerService.findAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BeerResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BeerType type,
            @RequestParam(required = false) BigDecimal minAbv,
            @RequestParam(required = false) BigDecimal maxAbv,
            @RequestParam(required = false) Long manufacturerId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        BeerFilter filter = new BeerFilter(name, type, minAbv, maxAbv, manufacturerId);
        return ResponseEntity.ok(beerService.search(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(beerService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    public ResponseEntity<BeerResponse> create(
            @Valid @RequestBody BeerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beerService.create(request, principal.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    public ResponseEntity<BeerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BeerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(beerService.update(id, request, principal.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        beerService.delete(id, principal.getId());
    }
}
