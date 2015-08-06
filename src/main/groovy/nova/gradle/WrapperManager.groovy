package nova.gradle

import groovy.transform.CompileStatic
import nova.gradle.extensions.WrapperConfigExtension
import nova.gradle.wrappers.MinecraftWrapper
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

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

	static Wrapper getWrapper(WrapperConfigExtension wrapperConfig, Locality locality) {
		wrappers.find { it.canHandle(wrapperConfig, locality) }
	}

	static JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension wrapperConfig, Locality locality, Configuration config) {
		def instancePath = project.rootDir.toPath().resolve("run/$wrapperConfig.name/$locality")
		instancePath.toFile().mkdirs()

		return getWrapper(wrapperConfig, locality).getLaunch(project, wrapperConfig, locality, instancePath, config)

		throw new WrapperNotFoundException("Could not find wrapper for $wrapperConfig $locality")
	}

	public static class WrapperNotFoundException extends RuntimeException {
		WrapperNotFoundException(String message) {
			super(message)
		}
	}
}
