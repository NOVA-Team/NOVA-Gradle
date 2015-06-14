package nova.gradle.extensions

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

public class NovaExtension {

	final NamedDomainObjectContainer<WrapperConfigExtension> wrappers;

	def wrappers(Closure config) {
		wrappers.configure(config)
	}

	public NovaExtension(Project project) {
		wrappers = project.container(WrapperConfigExtension)
	}
}
