package nova.gradle

import nova.gradle.extensions.NovaExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Matcher
import java.util.regex.Pattern

class NovaGradle implements Plugin<Project> {

	Pattern taskPattern = Pattern.compile("run(.+)(Server|Client)")

	@Override
	void apply(Project project) {
		if (!(project.plugins.hasPlugin("java") || project.plugins.hasPlugin("groovy") || project.plugins.hasPlugin("scala"))) {
			throw new GradleException("Please apply the java, scala or groovy plugin!")
		}

		project.extensions.create("nova", NovaExtension, project)

		project.repositories {
			jcenter()
			maven {
				name = "NovaAPI"
				url = "http://maven.novaapi.net/"
			}
		}

		addRules(project)
	}

	def addRules(Project project) {
		project.tasks.addRule("Pattern: run<Wrapper>Server") {}
		project.tasks.addRule("Pattern: run<Wrapper>Client") { String taskName ->
			Matcher match = taskPattern.matcher(taskName)
			if (match.matches()) {
				def wrapper = match.group(1)
				def locality = Locality.fromString(match.group(2))

				def launch = WrapperManager.getLaunch(project, project.nova.wrappers[wrapper], locality)

				launch.makeJavaExec(project, taskName)
			}
		}
	}
}
