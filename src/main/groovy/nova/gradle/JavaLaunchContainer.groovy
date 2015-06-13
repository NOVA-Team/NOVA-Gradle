package nova.gradle

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar

@CompileStatic
@Immutable
class JavaLaunchContainer {
	List<File> extraClasspath
	List<String> jvmArgs, launchArgs
	String mainClass

	JavaExec configureJavaExec(Project p, JavaExec exec) {
		def jarTask = p.tasks["jar"] as Jar

		exec.classpath(p.files(extraClasspath))
			.classpath(p.configurations["runtime"])
			.classpath(jarTask.archivePath)
			.jvmArgs(jvmArgs)
			.setArgs(launchArgs)
			.setMain(mainClass)
	}
}
