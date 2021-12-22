package org.archifacts.example.restbucks.descriptor;

import org.archifacts.core.descriptor.ArtifactContainerDescriptor;

public final class SpringRestbucksDescriptors {

	private SpringRestbucksDescriptors() {
	}

	public static final class BuildingBlockDescriptors {

		private BuildingBlockDescriptors() {
		}


	}

	public static final class RelationshipDescriptors {

		private RelationshipDescriptors() {
		}


	}

	public static final class ContainerDescriptors {

		private ContainerDescriptors() {
		}

		public static final ArtifactContainerDescriptor ModuleDescriptor = new ModuleDescriptor("org.springsource.restbucks");

	}

}
