package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "resource")
public class Resource {

    @Id
    private String id;

    private String name;

    private String category;

    protected Resource() {
    }

    // getters


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}