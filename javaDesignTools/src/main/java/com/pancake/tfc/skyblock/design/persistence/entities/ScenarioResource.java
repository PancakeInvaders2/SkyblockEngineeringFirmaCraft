package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "scenario_resource")
public class ScenarioResource {

    @EmbeddedId
    private ScenarioResourceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(name = "is_infinite", nullable = false)
    private boolean infinite;

    protected ScenarioResource() {
    }

    // getters

    public Resource getResource() {
        return resource;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public ScenarioResourceId getId() {
        return id;
    }

    public boolean isInfinite() {
        return infinite;
    }
}