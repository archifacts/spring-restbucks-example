package org.archifacts.example.restbucks.model;

import java.util.Set;

public class RestbucksApplication {

	private final Set<RestbucksModule> modules;

	RestbucksApplication(Set<RestbucksModule> modules) {
		this.modules = modules;
	}

	public Set<RestbucksModule> getModules() {
		return modules;
	}

}
