package org.archifacts.example.restbucks.asciidoc;

import java.util.HashMap;
import java.util.Map;

import org.archifacts.core.model.Artifact;
import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.core.model.ArtifactRelationship;
import org.archifacts.core.model.BuildingBlock;
import org.archifacts.core.model.MiscArtifact;
import org.archifacts.core.model.ExternalArtifact;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;

public final class C4ModelRepository {

	private final Workspace workspace;
	private final SoftwareSystem softwareSystem;
	private final Map<ArtifactContainer, Container> containerMap = new HashMap<>();
	private final Map<Artifact, Component> componentMap = new HashMap<>();
	private final Map<ArtifactRelationship, Relationship> relationshipMap = new HashMap<>();

	public C4ModelRepository(Workspace workspace) {
		this.workspace = workspace;
		this.softwareSystem = initSoftwareSystem(workspace);
	}
	
	private SoftwareSystem initSoftwareSystem(Workspace workspace) {
		final Model model = workspace.getModel();
		model.setImpliedRelationshipsStrategy(new CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy());
		return model.addSoftwareSystem(workspace.getName());
	}

	public Container container(ArtifactContainer artifactContainer) {
		return containerMap.computeIfAbsent(artifactContainer, a -> softwareSystem.addContainer(a.getName(), null, a.getType().getName()));
	}

	public Component artifact(Artifact artifact) {
		return artifact.getContainer().map(
				container -> componentMap.computeIfAbsent(artifact,
						b -> container(container).addComponent(artifact.getName(), artifact.getJavaClass().getName(), null,
								getTypeName(artifact))))
				.orElseThrow( () -> new IllegalStateException("No container present"));
	}

	public Relationship relationship(ArtifactRelationship relationship) {
		return relationshipMap.computeIfAbsent(relationship, rel -> artifact(rel.getSource()).uses(artifact(rel.getTarget()), rel.getRole().getName()));
	}

	private String getTypeName(Artifact artifact) {

		if (artifact instanceof BuildingBlock buildingBlock) {
			return buildingBlock.getType().getName();
		}
		if (artifact instanceof ExternalArtifact) {
			return "External";
		}
		if (artifact instanceof MiscArtifact) {
			return "Misc";
		}
		throw new IllegalArgumentException("Unexpected type: " + artifact.getClass().getName());
	}
	
	public Workspace workspace() {
		return workspace;
	}
	
	public SoftwareSystem softwareSystem() {
		return softwareSystem;
	}


}
