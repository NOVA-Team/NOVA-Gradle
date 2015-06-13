package nova.gradle

import groovy.transform.CompileStatic
import nova.gradle.extensions.WrapperConfigExtension
import nova.gradle.wrappers.MinecraftWrapper
import org.gradle.api.Project

import java.nio.file.Path

/**
 * @author rx14
 */
@CompileStatic
class WrapperManager {
	public static final List<Wrapper> wrappers = new ArrayList<>()

	static {
		add(new MinecraftWrapper())
	}

	static void add(Wrapper w) {
		wrappers.add(w)
	}

	static JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension extension, Locality locality) {
		for (wrapper in wrappers) {
			if (wrapper.canHandle(extension, locality)) {
				def instancePath = project.rootDir.toPath().resolve("run/$extension.name/$locality")
				instancePath.toFile().mkdirs()

				return wrapper.getLaunch(project, extension, locality, instancePath)
			}
		}

		throw new WrapperNotFoundException("Could not find wrapper for $extension $locality")
	}

	public static class WrapperNotFoundException extends RuntimeException {
		WrapperNotFoundException(String message) {
			super(message)
		}
	}
}
