package org.archifacts.example.restbucks.model;

import java.util.HashSet;
import java.util.Set;

import org.archifacts.core.model.BuildingBlock;

public class RestbucksEntity {
	private final BuildingBlock buildingBlock;
	private RestbucksModule module;
	private RestbucksEntity containingEntity;
	private RestbucksRepository managingRepository;
	private final Set<RestbucksEntity> containedEntities = new HashSet<>();

	RestbucksEntity(BuildingBlock buildingBlock) {
		this.buildingBlock = buildingBlock;
	}

	void setModule(RestbucksModule module) {
		this.module = module;
	}

	void addContainedEntity(RestbucksEntity entity) {
		containedEntities.add(entity);
	}

	void setContainingEntity(RestbucksEntity entity) {
		if (containingEntity != null) {
			throw new IllegalStateException("There already exists a containing entity.");
		}
		containingEntity = entity;
	}
	
	void setManagingRepository(RestbucksRepository repository) {
		if (managingRepository != null) {
			throw new IllegalStateException("There already exists a managing repository.");
		}
		managingRepository = repository;
	}

	public String getName() {
		return buildingBlock.getName();
	}

	public BuildingBlock getBuildingBlock() {
		return buildingBlock;
	}

	public Set<RestbucksEntity> getContainedEntities() {
		return containedEntities;
	}
	
	public RestbucksRepository getManagingRepository() {
		return managingRepository;
	}
	
	public RestbucksModule getModule() {
		return module;
	}

}
