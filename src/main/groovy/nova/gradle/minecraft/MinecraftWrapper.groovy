package nova.gradle.minecraft

import groovy.transform.CompileStatic
import nova.gradle.Locality
import nova.gradle.Wrapper
import org.gradle.api.Project

@CompileStatic
class MinecraftWrapper implements Wrapper {
	@Override
	String getName() {
		"MC"
	}

	@Override
	void addTask(Project project, String taskName, Locality locality, Map<String, String> options) {
		//
	}
}
