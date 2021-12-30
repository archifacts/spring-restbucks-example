package org.archifacts.example.restbucks.asciidoc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.archifacts.core.model.Application;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;
import org.archifacts.integration.asciidoc.AsciiDoc;

public class AsciiDocWriter {

	public void writeAsciidoc(final Application application, final Path outputFile) throws IOException {

		final AsciiDoc asciiDoc = new AsciiDoc("Spring Restbucks");
		application.getContainersOfType(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor.type())
				.stream()
				.map(ModuleAsciiDoc::new)
				.forEach(asciiDoc::addDocElement);

		try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
			asciiDoc.writeToWriter(writer);
		}

		System.out.println("Asciidoc written to " + outputFile.toString());
	}
}
