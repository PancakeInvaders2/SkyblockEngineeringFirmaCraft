package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scenario")
public class Scenario {

    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "scenario")
    private Set<ScenarioResource> resources = new HashSet<>();

    protected Scenario() {
    }

    // getters


    public Set<ScenarioResource> getResources() {
        return resources;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}