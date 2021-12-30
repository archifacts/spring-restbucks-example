package org.archifacts.example.restbucks.asciidoc;

import java.util.HashMap;
import java.util.Map;

import org.archifacts.core.model.Artifact;
import org.archifacts.core.model.ArtifactRelationship;
import org.archifacts.core.model.BuildingBlock;
import org.archifacts.core.model.ExternalArtifact;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Relationship;

public final class C4ModelRepository {

	private final Container c4Container;
	private final Map<Artifact, Component> componentMap = new HashMap<>();

	public C4ModelRepository(Container c4Container) {
		this.c4Container = c4Container;
	}

	public Component artifact(Artifact artifact) {
		return componentMap.computeIfAbsent(artifact, b -> c4Container.addComponent(artifact.getName(), artifact.getJavaClass().getName(), null,
				getTypeName(artifact)));
	}
	
	public Relationship relationship(ArtifactRelationship relationship) {
		return artifact(relationship.getSource()).uses(artifact(relationship.getTarget()), relationship.getRole().getName());
	}

	private String getTypeName(Artifact artifact) {

		if (artifact instanceof BuildingBlock buildingBlock) {
			return buildingBlock.getType().getName();
		}
		if (artifact instanceof ExternalArtifact) {
			return "External";
		}
		if (artifact instanceof ExternalArtifact) {
			return "Misc";
		}
		throw new IllegalArgumentException("Unexpected type: " + artifact.getClass().getName());
	}

}
