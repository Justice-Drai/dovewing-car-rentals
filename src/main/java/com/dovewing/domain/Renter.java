package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Renter {
    private final String name;
    private final RenterType kind;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType kind) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Renter name must not be blank: " + name);
        }
        Objects.requireNonNull(kind, "Renter type is required");

        this.name = name;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public RenterType getKind() {
        return kind;
    }

    void addHistory(String entry) {
        history.add(entry);
    }
}