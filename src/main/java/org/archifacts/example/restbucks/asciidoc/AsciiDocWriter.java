package org.archifacts.example.restbucks.asciidoc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.archifacts.core.model.Application;
import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;
import org.archifacts.integration.asciidoc.AsciiDoc;
import org.archifacts.integration.c4.model.C4Model;
import org.archifacts.integration.c4.model.C4ModelBuilder;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;
import org.archifacts.integration.spring.SpringDescriptors;

import com.structurizr.Workspace;

public class AsciiDocWriter {

	public void writeAsciidoc(final Application application, final Path outputFile) throws IOException {

		final AsciiDoc asciiDoc = new AsciiDoc("Spring Restbucks");

		final Workspace c4Workspace = initC4Workspace();

		final C4ModelBuilder c4ModelBuilder = C4Model.builder(c4Workspace);

		application.getRelationshipsOfRoles(
				JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor.role(),
				JMoleculesDescriptors.RelationshipDescriptors.IdentifiedByDescriptor.role(),
				JMoleculesDescriptors.RelationshipDescriptors.AggregateRootAssociationDescriptor.role(),
				SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor.role())
				.stream()
				.forEach(c4ModelBuilder::relationship);
		final C4Model c4Model = c4ModelBuilder.build();

//		final C4ModelRepository c4ModelRepository = new C4ModelRepository(c4Workspace);
//
//		application.getRelationshipsOfRoles(
//				JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor.role(),
//				JMoleculesDescriptors.RelationshipDescriptors.IdentifiedByDescriptor.role(),
//				JMoleculesDescriptors.RelationshipDescriptors.AggregateRootAssociationDescriptor.role(),
//				SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor.role())
//				.stream()
//				.forEach(c4ModelRepository::relationship);
//
		final List<ArtifactContainer> modules = application.getContainersOfType(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor.type())
				.stream()
				.filter(module -> !module
						.getBuildingBlocksOfTypes(JMoleculesDescriptors.BuildingBlockDescriptors.AggregateRootDescriptor.type(), JMoleculesDescriptors.BuildingBlockDescriptors.EntityDescriptor.type())
						.isEmpty())
				.sorted()
				.toList();

		asciiDoc.addDocElement(new ModulesAsciiDoc(modules, c4Model));

		try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
			asciiDoc.writeToWriter(writer);
		}

		System.out.println("Asciidoc written to " + outputFile.toString());
	}

	private Workspace initC4Workspace() {
		return new Workspace("Spring Restbucks", null);
	}
}
