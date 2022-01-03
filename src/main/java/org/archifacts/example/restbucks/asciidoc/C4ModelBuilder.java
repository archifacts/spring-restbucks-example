package org.archifacts.example.restbucks.asciidoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.archifacts.core.model.Archifact;
import org.archifacts.core.model.Artifact;
import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.core.model.ArtifactRelationship;
import org.archifacts.core.model.BuildingBlock;
import org.archifacts.core.model.ExternalArtifact;
import org.archifacts.core.model.MiscArtifact;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.ModelItem;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;

public class C4ModelBuilder {

	private final List<ArtifactContainer> containers = new ArrayList<>();
	private final List<Artifact> artifacts = new ArrayList<>();
	private final List<ArtifactRelationship> relationships = new ArrayList<>(); 

	private final Lookup lookup = new Lookup();
	private final C4ModelComputer<ArtifactContainer> containerComputer = new C4ModelComputer<>(lookup, this::defaultTransformation);
	private final C4ModelComputer<Artifact> artifactComputer = new C4ModelComputer<>(lookup, this::defaultTransformation);
	private final C4ModelComputer<ArtifactRelationship> relationshipComputer = new C4ModelComputer<>(lookup, this::defaultTransformation);

	private final Workspace workspace;
	private final SoftwareSystem softwareSystem;

	private final Set<ModelItem> defaultTransformation(Artifact artifact, C4ModelLookup lookup) {
		return artifact.getContainer().map(
				container -> Collections.<ModelItem>singleton(lookup.container(container).addComponent(artifact.getName(), artifact.getJavaClass().getName(), null,
						getTypeName(artifact))))
				.orElseThrow(() -> new IllegalStateException("No container present"));
	}

	private final Set<ModelItem> defaultTransformation(ArtifactRelationship relationship, C4ModelLookup lookup) {
		return Collections.<ModelItem>singleton(lookup.component(relationship.getSource())
				.uses(lookup.component(relationship.getTarget()), relationship.getRole().getName()));
	}

	private final Set<ModelItem> defaultTransformation(ArtifactContainer container, C4ModelLookup lookup) {
		return Collections.<ModelItem>singleton(lookup.softwareSystem().addContainer(container.getName(),
				null, container.getType().getName()));
	}

	public C4ModelBuilder(Workspace workspace) {
		this.workspace = workspace;
		this.softwareSystem = initSoftwareSystem(workspace);
	}

	private SoftwareSystem initSoftwareSystem(Workspace workspace) {
		final Model model = workspace.getModel();
		model.setImpliedRelationshipsStrategy(new CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy());
		return model.addSoftwareSystem(workspace.getName());
	}

	public void addContainer(ArtifactContainer artifactContainer) {
		containers.add(artifactContainer);
	}

	public void addArtifact(Artifact artifact) {
		artifacts.add(artifact);
	}

	public void addRelationship(ArtifactRelationship relationship) {
		relationships.add(relationship);
	}


	public C4Model build() {
		containers.forEach(containerComputer::compute);
		artifacts.forEach(artifactComputer::compute);
		relationships.forEach(relationshipComputer::compute);

		Map<Archifact, Set<ModelItem>> archifactMap = new HashMap<>();
		archifactMap.putAll(containerComputer.getMappings());
		archifactMap.putAll(artifactComputer.getMappings());
		archifactMap.putAll(relationshipComputer.getMappings());
		return new C4Model(workspace, softwareSystem, archifactMap);
	}

	private class Lookup implements C4ModelLookup {

		@Override
		public SoftwareSystem softwareSystem() {
			return softwareSystem;
		}

		private <T extends Archifact, R extends ModelItem> R lookup(T archifact, C4ModelComputer<T> computer, Class<R> returnType) {
			final Set<ModelItem> modelItems = computer.compute(archifact);
			if (modelItems.isEmpty()) {
				throw new IllegalStateException("No model item found for " + archifact);
			}
			if (modelItems.size() > 1) {
				throw new IllegalStateException("Too many model items found for " + archifact);
			}
			final ModelItem modelItem = modelItems.iterator().next();
			if (returnType.isInstance(modelItem)) {
				return returnType.cast(modelItem);
			} else {
				throw new IllegalStateException("Element is not of expected type: " + archifact);
			}
		}
		
		@Override
		public Component component(Artifact artifact) {
			return lookup(artifact, artifactComputer, Component.class);
		}

		@Override
		public Container container(ArtifactContainer artifactContainer) {
			return lookup(artifactContainer, containerComputer, Container.class);
		}

		@Override
		public Relationship relationship(ArtifactRelationship artifactRelationship) {
			return lookup(artifactRelationship, relationshipComputer, Relationship.class);
		}

	}

	private static String getTypeName(Artifact artifact) {

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
}
