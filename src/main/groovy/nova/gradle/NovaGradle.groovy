package nova.gradle

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import nova.gradle.extensions.NovaExtension
import nova.gradle.extensions.WrapperConfigExtension
import nova.gradle.util.FileLogListener
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.xml.XmlTransformer
import org.gradle.plugins.ide.idea.GenerateIdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.model.IdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModuleIml
import org.gradle.plugins.ide.idea.model.PathFactory
import org.w3c.dom.Element

import java.util.regex.Pattern

@CompileStatic
class NovaGradle implements Plugin<Project> {

	Pattern taskPattern = Pattern.compile("run(.+)(Server|Client)")

	@Override
	void apply(Project project) {
		//Set up logging
		def projectCacheDir = project.gradle.startParameter.projectCacheDir
		if (!projectCacheDir) {
			projectCacheDir = new File(project.projectDir, ".gradle")
		}

		def listener = new FileLogListener(new File(projectCacheDir, "gradle.log"))
		project.logging.addStandardOutputListener(listener)
		project.logging.addStandardErrorListener(listener)
		project.gradle.addBuildListener(listener)

		//Nova build extension
		project.extensions.create("nova", NovaExtension, project)

		//Add jcenter and NOVA repos
		project.repositories.with {
			add(jcenter())
			add(maven { MavenArtifactRepository repo ->
				repo.name = "NovaAPI"
				repo.url = "http://maven.novaapi.net/"
			})
		}

		//Add afterEvaluate hook
		project.afterEvaluate(this.&afterEvaluate)
	}

	def afterEvaluate(Project project) {
		//We need the runtime config
		if (!project.configurations["runtime"]) {
			throw new GradleException("Runtime configuration does not exist, make sure you have applied the java, scala or groovy plugins.")
		}

		//Add tasks for each wrapper configured
		def nova = project.extensions["nova"] as NovaExtension
		nova.wrappers.each { WrapperConfigExtension wrapper ->
			//TODO: Implement server locality
			Locality locality = Locality.Client

			addExecTask(project, wrapper, locality)
		}
	}

	JavaExec addExecTask(Project project, WrapperConfigExtension wrapper, Locality locality) {
		project.tasks.create("run$wrapper.name$locality", JavaExec).doFirst { JavaExec task ->
			WrapperManager
				.getLaunch(project, wrapper, locality)
				.configureJavaExec(project, task)
		}.dependsOn(project.tasks["jar"]) as JavaExec
	}
}
