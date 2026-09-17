package com.pancake.tfc.skyblock.design.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "technology_resource_requirement")
public class TechnologyResourceRequirement {

    @EmbeddedId
    private TechnologyResourceRequirementId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("technologyId")
    @JoinColumn(name = "technology_id")
    private Technology technology;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;

    protected TechnologyResourceRequirement() {
    }

    public TechnologyResourceRequirement(
            Technology technology,
            int requirementGroup,
            Resource resource
    ) {
        this.technology = technology;
        this.resource = resource;
        this.id = new TechnologyResourceRequirementId(
                technology.getId(),
                requirementGroup,
                resource.getId()
        );
    }

    public TechnologyResourceRequirementId getId() {
        return id;
    }

    public Technology getTechnology() {
        return technology;
    }

    public Resource getResource() {
        return resource;
    }

    public int getRequirementGroup() {
        return id.requirementGroup();
    }

    @Override
    public String toString() {
        String sb = "[" +
                "id:" + id +
                "resource:" + resource.getId() +
                "]";
        return sb;
    }}