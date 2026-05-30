package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.request.ManufacturerRequest;
import com.haufe.beercatalogue.dto.response.ManufacturerResponse;
import com.haufe.beercatalogue.security.UserPrincipal;
import com.haufe.beercatalogue.service.ManufacturerService;
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

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public ResponseEntity<Page<ManufacturerResponse>> findAll(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(manufacturerService.findAll(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    public ResponseEntity<ManufacturerResponse> create(
            @Valid @RequestBody ManufacturerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    public ResponseEntity<ManufacturerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ManufacturerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(manufacturerService.update(id, request, principal.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANUFACTURER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        manufacturerService.delete(id, principal.getId());
    }
}
