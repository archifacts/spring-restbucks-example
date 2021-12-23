package org.archifacts.example.restbucks.asciidoc;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.archifacts.example.restbucks.model.SpringRestbucksAggregateRoot;
import org.archifacts.example.restbucks.model.SpringRestbucksEntity;
import org.archifacts.example.restbucks.model.SpringRestbucksModule;
import org.archifacts.integration.asciidoc.AsciiDocElement;
import org.archifacts.integration.asciidoc.CompositeAsciiDocElement;
import org.archifacts.integration.asciidoc.TextDocElement;
import org.archifacts.integration.c4.asciidoc.plantuml.ComponentViewPlantUMLDocElement;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;

public class ModuleAsciiDoc implements AsciiDocElement {

	private final SpringRestbucksModule module;
	private final CompositeAsciiDocElement.Builder compositeAsciiDocElementBuilder = CompositeAsciiDocElement.builder();
	private final CompositeAsciiDocElement compositeAsciiDocElement;

	public ModuleAsciiDoc(SpringRestbucksModule module) {
		this.module = module;
		compositeAsciiDocElementBuilder.element(new TextDocElement("== " + module.getName()));
		
		final Workspace c4Workspace = initC4Workspace();
		final SoftwareSystem c4SoftwareSystem = initC4SoftwareSystem(c4Workspace);
		final Container c4Container = initC4Container(c4SoftwareSystem);
		final ViewSet c4Views = c4Workspace.getViews();
		
		final Map<SpringRestbucksAggregateRoot, Component> aggregateRootMap = module.getAggregateRoots()
				.stream()
				.collect(Collectors.toMap(Function.identity(), aggregateRoot -> c4Container.addComponent(aggregateRoot.getName(), aggregateRoot.getBuildingBlock().getJavaClass().getName(), null,
						aggregateRoot.getBuildingBlock().getType().getName())));
		final Map<SpringRestbucksEntity, Component> entitiesMap = module.getEntities()
				.stream()
				.collect(Collectors.toMap(Function.identity(), aggregateRoot -> c4Container.addComponent(aggregateRoot.getName(), aggregateRoot.getBuildingBlock().getJavaClass().getName(), null,
						aggregateRoot.getBuildingBlock().getType().getName())));
		
		System.out.println("TEST1");
		aggregateRootMap.forEach((aggregateRoot, c4Component) -> {
			aggregateRoot.getContainedEntities().forEach(containedEntity -> {
				System.out.println("TEST");
				System.out.println(aggregateRoot + " -> " + containedEntity);
				c4Component.uses(entitiesMap.getOrDefault(containedEntity, aggregateRootMap.get(containedEntity)), null, "contains");
			});
		});
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
		return  model.addSoftwareSystem(workspace.getName());
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
