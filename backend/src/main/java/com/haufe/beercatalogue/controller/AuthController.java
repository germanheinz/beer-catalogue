package com.haufe.beercatalogue.controller;

import com.haufe.beercatalogue.dto.response.AuthResponse;
import com.haufe.beercatalogue.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        var user = principal.getUser();
        Long manufacturerId = user.getManufacturer() != null ? user.getManufacturer().getId() : null;
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getUsername(), user.getRole(), manufacturerId));
    }
}
