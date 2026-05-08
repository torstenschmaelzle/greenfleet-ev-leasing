package io.carlease.auth;

public record AuthUser(String username, String role, String name, String email, String company) {}
