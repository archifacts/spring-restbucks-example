package org.archifacts.example.restbucks.model;

import org.archifacts.core.model.BuildingBlock;

public class RestbucksRepository {
	private final BuildingBlock buildingBlock;
	private RestbucksModule module;
	private RestbucksEntity managedEntity;

	RestbucksRepository(BuildingBlock buildingBlock) {
		this.buildingBlock = buildingBlock;
	}

	void setModule(RestbucksModule module) {
		this.module = module;
	}

	void setManagedEntity(RestbucksEntity entity) {
		if (managedEntity != null) {
			throw new IllegalStateException("There already exists a managed entity.");
		}
		managedEntity = entity;
	}

	public String getName() {
		return buildingBlock.getName();
	}

	public BuildingBlock getBuildingBlock() {
		return buildingBlock;
	}

	public RestbucksModule getModule() {
		return module;
	}

}
