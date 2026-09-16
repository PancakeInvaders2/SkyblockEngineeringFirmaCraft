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

    @OneToMany(
            mappedBy = "process",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ProcessInput> inputs = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "process_output",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> outputs = new HashSet<>();

    @ManyToMany(mappedBy = "unlockedProcesses")
    private Set<Technology> unlockedBy = new HashSet<>();

    public Process(){}

    public Process(
            String id,
            String name,
            String type,
            boolean technologyRequired
    ) {
        this(id, name, type, technologyRequired, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    public Process(
            String id,
            String name,
            String type,
            boolean technologyRequired,
            Set<ProcessInput> inputs,
            Set<Resource> outputs,
            Set<Technology> unlockedBy ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.technologyRequired = technologyRequired;
        this.inputs = inputs;
        this.outputs = outputs;
        this.unlockedBy = unlockedBy;
    }

    // getters


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Set<ProcessInput> getInputs() {
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

    public void addInput(
            int inputGroup,
            Resource resource
    ) {
        inputs.add(new ProcessInput(this, inputGroup, resource));
    }

    public void addOutput(
            Resource resource
    ) {
        outputs.add(resource);
    }

    public void setTechnologyRequired(boolean technologyRequired) {
        this.technologyRequired = technologyRequired;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(id).append(", ");
        sb.append("name: ").append(name).append(", ");
        sb.append("type: ").append(type).append(", ");
        sb.append("technologyRequired: ").append(technologyRequired).append(", ");
        sb.append("inputs: ").append(inputs).append(", ");
        sb.append("outputs: ").append(outputs).append(", ");
        sb.append("unlockedBy: ").append(unlockedBy);
        return sb.toString();
    }

}