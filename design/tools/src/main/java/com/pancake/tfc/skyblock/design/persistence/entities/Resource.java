package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "resource")
public class Resource {

    @Id
    private String id;

    public Resource(){}

    public Resource(String id) {
        this.id = id;
    }

    // getters


    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id ;
    }
}