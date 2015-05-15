package nova.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
interface Wrapper {
	String getName();

	void addTask(Project project, String taskName, Locality locality, Map<String, String> options);
}
