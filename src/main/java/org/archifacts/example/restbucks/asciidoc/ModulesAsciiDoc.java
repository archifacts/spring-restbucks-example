package org.archifacts.example.restbucks.asciidoc;

import java.util.List;

import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.integration.asciidoc.AsciiDocElement;
import org.archifacts.integration.asciidoc.CompositeAsciiDocElement;
import org.archifacts.integration.asciidoc.TextDocElement;
import org.archifacts.integration.c4.asciidoc.plantuml.ContainerViewPlantUMLDocElement;
import org.archifacts.integration.c4.model.C4Model;

import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;

public class ModulesAsciiDoc implements AsciiDocElement {

	private final CompositeAsciiDocElement.Builder compositeAsciiDocElementBuilder = CompositeAsciiDocElement.builder();
	private final CompositeAsciiDocElement compositeAsciiDocElement;

	public ModulesAsciiDoc(List<ArtifactContainer> modules, C4Model c4Model) {
		compositeAsciiDocElementBuilder.element(new TextDocElement("== Modules"));

		final ViewSet c4Views = c4Model.workspace().getViews();

		final List<Container> containers = modules
				.stream()
				.map(module -> c4Model.container(module))
				.toList();
		final ContainerView containerView = initC4ContainerView(c4Model.softwareSystem(), containers, c4Views);
		compositeAsciiDocElementBuilder.element(new ContainerViewPlantUMLDocElement(containerView));

		modules
				.stream()
				.map(module -> new ModuleAsciiDoc(module, c4Model))
				.forEach(compositeAsciiDocElementBuilder::element);

		compositeAsciiDocElement = compositeAsciiDocElementBuilder.build();
	}

	private ContainerView initC4ContainerView(final SoftwareSystem softwareSystem, List<Container> containers, final ViewSet views) {
		final ContainerView containerView = views.createContainerView(softwareSystem, "modules", null);
		containerView.addAllElements();
		containerView.enableAutomaticLayout();
		return containerView;

	}

	@Override
	public String render() {
		return compositeAsciiDocElement.render();
	}

}
