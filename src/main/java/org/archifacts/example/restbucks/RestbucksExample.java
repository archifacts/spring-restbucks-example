package org.archifacts.example.restbucks;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.archifacts.core.model.Application;
import org.archifacts.example.restbucks.asciidoc.AsciiDocWriter;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;
import org.archifacts.integration.plaintext.ApplicationOverview;
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
			writeApplicationOverviewToStdOut(application);
			Files.createDirectories(outputFolder);
			new AsciiDocWriter().writeAsciidoc(application, outputFolder.resolve("index.adoc"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void writeApplicationOverviewToStdOut(final Application application) throws IOException {
		try (PrintWriter writer = new PrintWriter(System.out, true)) {
			new ApplicationOverview(application).writeToWriter(writer);
		}
	}

	private Application initApplication(final JavaClasses javaClasses) {
		return Application.builder().addContainerDescriptor(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor)
				.addBuildingBlockDescriptor(JMoleculesDescriptors.BuildingBlockDescriptors.EventDescriptor)
				.addBuildingBlockDescriptor(JMoleculesDescriptors.BuildingBlockDescriptors.AggregateRootDescriptor)
				.addBuildingBlockDescriptor(JMoleculesDescriptors.BuildingBlockDescriptors.EntityDescriptor)
				.addBuildingBlockDescriptor(JMoleculesDescriptors.BuildingBlockDescriptors.IdentifierDescriptor)
				.addBuildingBlockDescriptor(SpringDescriptors.BuildingBlockDescriptors.ConfigurationDescriptor)
				.addBuildingBlockDescriptor(SpringDescriptors.BuildingBlockDescriptors.ControllerDescriptor)
				.addBuildingBlockDescriptor(SpringDescriptors.BuildingBlockDescriptors.RepositoryDescriptor)
				.addBuildingBlockDescriptor(SpringDescriptors.BuildingBlockDescriptors.ServiceDescriptor)
				.addBuildingBlockDescriptor(SpringDescriptors.BuildingBlockDescriptors.ComponentDescriptor)
				.addSourceBasedRelationshipDescriptor(JMoleculesDescriptors.RelationshipDescriptors.IdentifiedByDescriptor)
				.addSourceBasedRelationshipDescriptor(JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor)
				.addSourceBasedRelationshipDescriptor(SpringDescriptors.RelationshipDescriptors.EventListenerDescriptor)
				.addTargetBasedRelationshipDescriptor(SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor)
				.buildApplication(javaClasses);

	}

}
