package org.archifacts.example.restbucks.asciidoc;

import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.integration.asciidoc.AsciiDocElement;
import org.archifacts.integration.asciidoc.CompositeAsciiDocElement;
import org.archifacts.integration.asciidoc.TextDocElement;
import org.archifacts.integration.c4.asciidoc.plantuml.ComponentViewPlantUMLDocElement;

import com.structurizr.model.Container;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;

public class ModuleAsciiDoc implements AsciiDocElement {

	private final CompositeAsciiDocElement.Builder compositeAsciiDocElementBuilder = CompositeAsciiDocElement.builder();
	private final CompositeAsciiDocElement compositeAsciiDocElement;

	public ModuleAsciiDoc(ArtifactContainer module, C4ModelRepository c4ModelRepository) {
		compositeAsciiDocElementBuilder.element(new TextDocElement("== " + module.getName()));

		final ViewSet c4Views = c4ModelRepository.workspace().getViews();

		final Container c4Container = c4ModelRepository.container(module);

		final ComponentView c4ComponentView = initC4ComponentView(c4Container, c4Views);
		compositeAsciiDocElementBuilder.element(new ComponentViewPlantUMLDocElement(c4ComponentView));

		compositeAsciiDocElement = compositeAsciiDocElementBuilder.build();
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
