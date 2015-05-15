package nova.gradle

import groovy.transform.CompileStatic
import org.gradle.api.tasks.JavaExec

@CompileStatic
interface Wrapper {
	String getName();

	JavaExec getTask(Locality locality, Map<String, String> options);
}
