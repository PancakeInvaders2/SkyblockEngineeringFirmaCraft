package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "technology")
public class Technology {

    @Id
    private String id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "technology_resource_requirement",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> resourceRequirements = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "technology_process_unlock",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "process_id")
    )
    private Set<Process> unlockedProcesses = new HashSet<>();

    public Technology() {
    }

    public Technology(String id,
                      String name,
                      Set<Resource> resourceRequirements,
                      Set<Process> unlockedProcesses) {

        this.id = id;
        this.name = name;
        this.resourceRequirements = resourceRequirements;
        this.unlockedProcesses = unlockedProcesses;
    }

    // getters


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Set<Process> getUnlockedProcesses() {
        return unlockedProcesses;
    }

    public Set<Resource> getResourceRequirements() {
        return resourceRequirements;
    }
}