package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;

public class Renter {
    private final String name;
    private final RenterType kind;
    private final List<String> history = new ArrayList<>();

    public Renter(String name, RenterType kind) {
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