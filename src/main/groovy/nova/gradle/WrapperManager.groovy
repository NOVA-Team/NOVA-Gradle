package nova.gradle

import groovy.transform.CompileStatic
import nova.gradle.minecraft.MinecraftWrapper
import org.gradle.api.Project

/**
 * @author rx14
 */
@CompileStatic
class WrapperManager {
	public static final Map<String, Wrapper> wrappers = new HashMap<>()

	static {
		add(new MinecraftWrapper())
	}

	static void add(Wrapper w) {
		wrappers.put(w.name, w)
	}

	static void get(Project project, String taskName, String wrapper, Locality locality, Map<String, String> options) {
		Wrapper w = wrappers.get(wrapper)

		w.addTask(project, taskName, locality, options)
	}
}
