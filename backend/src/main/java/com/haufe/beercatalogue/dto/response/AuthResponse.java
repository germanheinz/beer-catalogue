package com.haufe.beercatalogue.dto.response;

import com.haufe.beercatalogue.domain.enums.Role;

public record AuthResponse(Long id, String username, Role role, Long manufacturerId) {}
