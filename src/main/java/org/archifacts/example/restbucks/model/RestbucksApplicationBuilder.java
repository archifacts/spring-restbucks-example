package org.archifacts.example.restbucks.model;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.archifacts.core.model.Application;
import org.archifacts.core.model.Artifact;
import org.archifacts.core.model.ArtifactContainer;
import org.archifacts.core.model.ArtifactRelationship;
import org.archifacts.core.model.BuildingBlock;
import org.archifacts.example.restbucks.descriptor.SpringRestbucksDescriptors;
import org.archifacts.integration.jmolecules.JMoleculesDescriptors;
import org.archifacts.integration.spring.SpringDescriptors;

public class RestbucksApplicationBuilder {

	private final Map<ArtifactContainer, RestbucksModule> moduleMap = new ConcurrentHashMap<>();
	private final Map<BuildingBlock, RestbucksAggregateRoot> aggregateRootMap = new ConcurrentHashMap<>();
	private final Map<BuildingBlock, RestbucksEntity> entityMap = new ConcurrentHashMap<>();
	private final Map<BuildingBlock, RestbucksRepository> repositoryMap = new ConcurrentHashMap<>();

	public RestbucksApplication transform(Application application) {

		application.getContainersOfType(SpringRestbucksDescriptors.ContainerDescriptors.ModuleDescriptor.type())
				.forEach(this::transformModule);

		application.getBuildingBlocksOfType(JMoleculesDescriptors.BuildingBlockDescriptors.AggregateRootDescriptor.type())
				.forEach(this::transformAggregateRoot);

		application.getBuildingBlocksOfType(JMoleculesDescriptors.BuildingBlockDescriptors.EntityDescriptor.type())
				.forEach(this::transformEntity);

		application.getBuildingBlocksOfType(SpringDescriptors.BuildingBlockDescriptors.RepositoryDescriptor.type())
				.forEach(this::transformRepository);

		application.getRelationshipsOfRole(JMoleculesDescriptors.RelationshipDescriptors.ContainedEntityDescriptor.role())
				.forEach(this::transformContainedEntity);

		application.getRelationshipsOfRole(SpringDescriptors.RelationshipDescriptors.ManagedByDescriptor.role())
				.forEach(this::transformEntityManagedByRepository);

		return new RestbucksApplication(new HashSet<>(moduleMap.values()));

	}

	private void transformModule(ArtifactContainer artifactContainer) {
		moduleMap.computeIfAbsent(artifactContainer, RestbucksModule::new);
	}

	private void transformAggregateRoot(BuildingBlock buildingBlock) {
		final RestbucksAggregateRoot aggregateRoot = aggregateRootMap.computeIfAbsent(buildingBlock, RestbucksAggregateRoot::new);
		entityMap.put(buildingBlock, aggregateRoot);
		buildingBlock.getContainer().ifPresent(container -> {
			final RestbucksModule module = moduleMap.get(container);
			module.addAggregateRoot(aggregateRoot);
			aggregateRoot.setModule(module);
		});
	}

	private void transformEntity(BuildingBlock buildingBlock) {
		final RestbucksEntity entity = entityMap.computeIfAbsent(buildingBlock, RestbucksEntity::new);
		buildingBlock.getContainer().ifPresent(container -> {
			final RestbucksModule module = moduleMap.get(container);
			module.addEntity(entity);
			entity.setModule(module);
		});
	}

	private void transformRepository(BuildingBlock buildingBlock) {
		final RestbucksRepository repository = repositoryMap.computeIfAbsent(buildingBlock, RestbucksRepository::new);
		buildingBlock.getContainer().ifPresent(container -> {
			final RestbucksModule module = moduleMap.get(container);
			module.addRepository(repository);
			repository.setModule(module);
		});
	}

	private void transformContainedEntity(ArtifactRelationship relationship) {
		final Artifact source = relationship.getSource();
		final Artifact target = relationship.getTarget();

		final RestbucksEntity containingEntity = entityMap.get(source);
		final RestbucksEntity containedEntity = entityMap.get(target);
		containingEntity.addContainedEntity(containedEntity);
		containedEntity.setContainingEntity(containingEntity);

	}
	
	private void transformEntityManagedByRepository(ArtifactRelationship relationship) {
		final Artifact source = relationship.getSource();
		final Artifact target = relationship.getTarget();
		
		final RestbucksEntity managedEntity = entityMap.getOrDefault(source, aggregateRootMap.get(source));
		final RestbucksRepository repository = repositoryMap.get(target);
		repository.setManagedEntity(managedEntity);
		managedEntity.setManagingRepository(repository);
		
	}

}
