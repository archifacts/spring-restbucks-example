package org.archifacts.example.restbucks.model;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.archifacts.core.model.ArtifactRelationship;
import org.archifacts.core.model.BuildingBlock;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;

public class SpringRestbucksEntity {

	private final BuildingBlock buildingBlock;

	SpringRestbucksEntity(final BuildingBlock buildingBlock) {
		this.buildingBlock = buildingBlock;
	}
	
	public String getName() {
		return buildingBlock.getName();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(buildingBlock);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		SpringRestbucksEntity other = (SpringRestbucksEntity) obj;
		return Objects.equals(buildingBlock, other.buildingBlock);
	}
	
	public BuildingBlock getBuildingBlock() {
		return buildingBlock;
	}
	
	
	public Set<SpringRestbucksEntity> getContainedEntities() {
		return buildingBlock.getOutgoingRelationshipsOfRole(JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor.role())
				.stream()
				.map(ArtifactRelationship::getTarget)
				.filter(BuildingBlock.class::isInstance)
				.map(BuildingBlock.class::cast)
				.map(SpringRestbucksEntity::new)
				.collect(Collectors.toSet());
	}

}
