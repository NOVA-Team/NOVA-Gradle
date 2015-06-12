package nova.gradle

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

@CompileStatic
@Immutable
class JavaLaunchContainer {
	List<File> classpath
	List<String> jvmArgs, launchArgs
	String mainClass

	JavaExec makeJavaExec(Project p, String taskName) {
		p.tasks.create(taskName, JavaExec)
			.setClasspath(p.files(classpath))
			.jvmArgs(jvmArgs)
			.setArgs(launchArgs)
			.setMain(mainClass)
	}
}
