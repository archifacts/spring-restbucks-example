package org.archifacts.example.restbucks.model;

import static java.util.Comparator.comparing;

import java.util.List;

import org.archifacts.core.model.Application;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;

public final class SpringRestbucksApplication {

	private final Application application;

	public SpringRestbucksApplication(final Application application) {
		this.application = application;
	}

	public List<SpringRestbucksModule> getModules() {
		return application.getContainersOfType(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor.type())
				.stream()
				.map(SpringRestbucksModule::new)
				.sorted(comparing(SpringRestbucksModule::getName))
				.toList();
	}
}
