package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;

public class Renter {
    public String name;
    public RenterType kind;
    public int id;
    public List<String> history = new ArrayList<>();
    static int counter = 1;

    public Renter(String name, RenterType kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }
}