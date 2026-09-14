package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "process")
public class Process {

    @Id
    private String id;

    private String name;

    private String type;

    private boolean technologyRequired;

    @ManyToMany
    @JoinTable(
            name = "process_input",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> inputs = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "process_output",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> outputs = new HashSet<>();

    @ManyToMany(mappedBy = "unlockedProcesses")
    private Set<Technology> unlockedBy = new HashSet<>();

    protected Process() {
    }

    // getters


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Set<Resource> getInputs() {
        return inputs;
    }

    public Set<Resource> getOutputs() {
        return outputs;
    }

    public String getType() {
        return type;
    }

    public boolean isTechnologyRequired() {
        return technologyRequired;
    }

    public Set<Technology> getUnlockedBy() {
        return unlockedBy;
    }
}