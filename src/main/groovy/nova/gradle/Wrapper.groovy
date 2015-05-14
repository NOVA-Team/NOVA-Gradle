package nova.gradle

import org.gradle.api.tasks.JavaExec

interface Wrapper {
	String getName();

	JavaExec getTask(Locality locality, Map<String, String> options);
}
