package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "process_input")
public class ProcessInput {

    @EmbeddedId
    private ProcessInputId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("processId")
    @JoinColumn(name = "process_id")
    private Process process;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;

    protected ProcessInput() {
    }

    public ProcessInput(
            Process process,
            int inputGroup,
            Resource resource
    ) {
        this.process = process;
        this.resource = resource;
        this.id = new ProcessInputId(
                process.getId(),
                inputGroup,
                resource.getId()
        );
    }

    public ProcessInputId getId() {
        return id;
    }

    public Process getProcess() {
        return process;
    }

    public Resource getResource() {
        return resource;
    }

    public int getInputGroup() {
        return id.inputGroup();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(id).append(", ");
        sb.append("resource: ").append(resource);
        return sb.toString();
    }
}