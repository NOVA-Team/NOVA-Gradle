package nova.gradle

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

@CompileStatic
@Immutable
class JavaLaunchContainer {
	List<File> extraClasspath
	List<String> jvmArgs, launchArgs
	String mainClass

	JavaExec makeJavaExec(Project p, String taskName) {
		p.tasks.create(taskName, JavaExec)
			.setClasspath(p.files(extraClasspath) + p.configurations["runtime"])
			.jvmArgs(jvmArgs)
			.setArgs(launchArgs)
			.setMain(mainClass)
	}
}
