package org.archifacts.example.restbucks.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.archifacts.core.model.ArtifactContainer;

public class RestbucksModule {

	private final ArtifactContainer artifactContainer;
	private final Set<RestbucksAggregateRoot> aggregateRoots = new HashSet<>();
	private final Set<RestbucksEntity> entities = new HashSet<>();
	private final Set<RestbucksRepository> repositories = new HashSet<>();

	RestbucksModule(ArtifactContainer artifactContainer) {
		this.artifactContainer = artifactContainer;
	}

	void addAggregateRoot(RestbucksAggregateRoot aggregateRoot) {
		aggregateRoots.add(aggregateRoot);
	}

	void addEntity(RestbucksEntity entity) {
		entities.add(entity);
	}

	void addRepository(RestbucksRepository repository) {
		repositories.add(repository);
	}

	public String getName() {
		return artifactContainer.getName();
	}

	public Set<RestbucksAggregateRoot> getAggregateRoots() {
		return aggregateRoots;
	}

	public Set<RestbucksEntity> getEntities() {
		return entities;
	}
	
	public Set<RestbucksRepository> getRepositories() {
		return repositories;
	}

	public Set<RestbucksEntity> getAllEntities() {
		return Stream.concat(
				aggregateRoots.stream(),
				entities.stream())
				.collect(Collectors.toSet());
	}

}
