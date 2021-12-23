package org.archifacts.example.restbucks.model;

import static java.util.Comparator.comparing;

import java.util.List;

import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;

public final class SpringRestbucksModule {
	private final ArtifactContainer artifactContainer;

	SpringRestbucksModule(final ArtifactContainer artifactContainer) {
		this.artifactContainer = artifactContainer;
	}

	public String getName() {
		return artifactContainer.getName();
	}

	public List<SpringRestbucksAggregateRoot> getAggregateRoots() {
		return artifactContainer.getBuildingBlocksOfType(JMoleculesDescriptors.BuildingBlockDescriptors.AggregateRootDescriptor.type())
				.stream()
				.map(SpringRestbucksAggregateRoot::new)
				.sorted(comparing(SpringRestbucksAggregateRoot::getName))
				.toList();
	}
	
	public List<SpringRestbucksEntity> getEntities() {
		return artifactContainer.getBuildingBlocksOfType(JMoleculesDescriptors.BuildingBlockDescriptors.EntityDescriptor.type())
				.stream()
				.map(SpringRestbucksEntity::new)
				.sorted(comparing(SpringRestbucksEntity::getName))
				.toList();
	}
}
