package com.homelink.api.entity;

/**
 * Role constants kept for backward compatibility. Use string-based roles stored
 * on the `User.roles` collection (e.g. "USER", "AGENT", "ADMIN").
 */
public final class Role {
    public static final String ADMIN = "ADMIN";
    public static final String AGENT = "AGENT";
    public static final String USER = "USER";

    private Role() {}
}
