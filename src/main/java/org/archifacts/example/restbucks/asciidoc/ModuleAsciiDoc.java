package org.archifacts.example.restbucks.asciidoc;

import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.integration.asciidoc.AsciiDocElement;
import org.archifacts.integration.asciidoc.CompositeAsciiDocElement;
import org.archifacts.integration.asciidoc.TextDocElement;
import org.archifacts.integration.c4.asciidoc.plantuml.ComponentViewPlantUMLDocElement;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;
import org.archifacts.integration.spring.SpringDescriptors;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;

public class ModuleAsciiDoc implements AsciiDocElement {

	private final ArtifactContainer module;
	private final CompositeAsciiDocElement.Builder compositeAsciiDocElementBuilder = CompositeAsciiDocElement.builder();
	private final CompositeAsciiDocElement compositeAsciiDocElement;

	public ModuleAsciiDoc(ArtifactContainer module) {
		this.module = module;
		compositeAsciiDocElementBuilder.element(new TextDocElement("== " + module.getName()));

		final Workspace c4Workspace = initC4Workspace();
		final SoftwareSystem c4SoftwareSystem = initC4SoftwareSystem(c4Workspace);
		final Container c4Container = initC4Container(c4SoftwareSystem);
		final ViewSet c4Views = c4Workspace.getViews();

		final C4ModelRepository c4ModelRepository = new C4ModelRepository(c4Container);

		module.getOutgoingRelationshipsOfRoles(
				JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor.role(),
				JMoleculesDescriptors.RelationshipDescriptors.IdentifiedByDescriptor.role(),
				SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor.role())
				.stream()
				.forEach(c4ModelRepository::relationship);

		if (!c4Container.getComponents().isEmpty()) {
			final ComponentView c4ComponentView = initC4ComponentView(c4Container, c4Views);
			compositeAsciiDocElementBuilder.element(new ComponentViewPlantUMLDocElement(c4ComponentView));
		} else {
			compositeAsciiDocElementBuilder.element(new TextDocElement("No Aggregate Roots or Entities present."));
		}
		compositeAsciiDocElement = compositeAsciiDocElementBuilder.build();
	}

	private Workspace initC4Workspace() {
		return new Workspace("Spring Restbucks", null);
	}

	private SoftwareSystem initC4SoftwareSystem(Workspace workspace) {
		final Model model = workspace.getModel();
		model.setImpliedRelationshipsStrategy(new CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy());
		return model.addSoftwareSystem(workspace.getName());
	}

	private Container initC4Container(SoftwareSystem softwareSystem) {
		return softwareSystem.addContainer(module.getName());
	}

	private ComponentView initC4ComponentView(final Container container, final ViewSet views) {

		final ComponentView componentView = views.createComponentView(container, container.getName(), null);
		componentView.addAllComponents();
		componentView.addExternalDependencies();
		return componentView;
	}

	@Override
	public String render() {
		return compositeAsciiDocElement.render();
	}

}
