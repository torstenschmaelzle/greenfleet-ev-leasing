package io.carlease.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerLoginRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String company
) {}
