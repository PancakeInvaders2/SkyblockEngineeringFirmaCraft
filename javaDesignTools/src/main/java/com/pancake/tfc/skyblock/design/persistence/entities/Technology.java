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

    @OneToMany(
            mappedBy = "technology",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<TechnologyResourceRequirement> resourceRequirements =
            new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "technology_process_unlock",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "process_id")
    )
    private Set<Process> unlockedProcesses = new HashSet<>();

    public Technology() {
    }

    public Technology(
            String id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Set<TechnologyResourceRequirement> getResourceRequirements() {
        return resourceRequirements;
    }

    public Set<Process> getUnlockedProcesses() {
        return unlockedProcesses;
    }

    public void addResourceRequirement(
            int requirementGroup,
            Resource resource
    ) {
        resourceRequirements.add(
                new TechnologyResourceRequirement(
                        this,
                        requirementGroup,
                        resource
                )
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Technology id:").append(id);
        int i = 0;
        for(TechnologyResourceRequirement resourceRequirement : resourceRequirements){
            sb.append(", resourceRequirements ").append(i).append(" size: ").append(resourceRequirement);
            i++;
        }
        sb.append(", unlockedProcesses size: ").append(unlockedProcesses.size());
        return sb.toString();
    }
}