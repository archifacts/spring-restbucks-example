package org.archifacts.example.restbucks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.archifacts.core.model.Application;
import org.archifacts.example.restbucks.asciidoc.AsciiDocWriter;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;
import org.archifacts.integration.spring.SpringDescriptors;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(description = "Generates a AsciiDoc based documentation of the Spring Restbucks application.")
public class RestbucksExample implements Runnable {

	@Parameters(index = "0", description = "The output folder")
	private Path outputFolder;

	private static final String ApplicationPackage = "org.springsource.restbucks";

	public static void main(final String[] args) {
		new CommandLine(new RestbucksExample()).execute(args);
	}

	@Override
	public void run() {
		final JavaClasses javaClasses = new ClassFileImporter().importPackages(ApplicationPackage);
		final Application application = initApplication(javaClasses);
		try {
			Files.createDirectories(outputFolder);
			new AsciiDocWriter().writeAsciidoc(application, outputFolder.resolve("index.adoc"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Application initApplication(final JavaClasses javaClasses) {
		return Application.builder()
				.descriptor(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor)
				.descriptor(JMoleculesDescriptors.BuildingBlockDescriptors.EventDescriptor)
				.descriptor(JMoleculesDescriptors.BuildingBlockDescriptors.AggregateRootDescriptor)
				.descriptor(JMoleculesDescriptors.BuildingBlockDescriptors.EntityDescriptor)
				.descriptor(JMoleculesDescriptors.BuildingBlockDescriptors.IdentifierDescriptor)
				.descriptor(SpringDescriptors.BuildingBlockDescriptors.ConfigurationDescriptor)
				.descriptor(SpringDescriptors.BuildingBlockDescriptors.ControllerDescriptor)
				.descriptor(SpringDescriptors.BuildingBlockDescriptors.RepositoryDescriptor)
				.descriptor(SpringDescriptors.BuildingBlockDescriptors.ServiceDescriptor)
				.descriptor(SpringDescriptors.BuildingBlockDescriptors.ComponentDescriptor)
				.descriptor(JMoleculesDescriptors.RelationshipDescriptors.IdentifiedByDescriptor)
				.descriptor(JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor)
				.descriptor(JMoleculesDescriptors.RelationshipDescriptors.AggregateRootAssociationDescriptor)
				.descriptor(SpringDescriptors.RelationshipDescriptors.EventListenerDescriptor)
				.descriptor(SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor)
				.buildApplication(javaClasses);

	}

}
