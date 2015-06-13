package nova.gradle

import nova.gradle.extensions.NovaExtension
import nova.gradle.extensions.WrapperConfigExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.xml.XmlTransformer
import org.gradle.plugins.ide.idea.GenerateIdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.model.IdeaModule
import org.gradle.plugins.ide.idea.model.IdeaModuleIml
import org.gradle.plugins.ide.idea.model.PathFactory
import org.w3c.dom.Element

import java.util.regex.Pattern

class NovaGradle implements Plugin<Project> {

	Pattern taskPattern = Pattern.compile("run(.+)(Server|Client)")

	@Override
	void apply(Project project) {
		project.extensions.create("nova", NovaExtension, project)

		project.repositories {
			jcenter()
			maven {
				name = "NovaAPI"
				url = "http://maven.novaapi.net/"
			}
		}

		project.afterEvaluate(this.&afterEvaluate)
	}

	def afterEvaluate(Project project) {
		if (!project.configurations.findByName("runtime")) {
			throw new GradleException("Runtime configuration does not exist, make sure you have applied the java, scala or groovy plugins.")
		}

		def idea = project.rootProject.extensions["idea"] as IdeaModel
		List<GenerateIdeaModule> tasks = []

		project.nova.wrappers.each { WrapperConfigExtension wrapper ->
			//TODO: Implement server locality
			Locality locality = Locality.Client

			addExecTask(project, wrapper, locality)
			tasks << addIdeaModuleTask(project, wrapper, locality)

			idea.project.ipr.withXml { XmlProvider xml ->
				generateIdeaRunconfig(xml.asElement(), wrapper, locality)
			}
		}

		idea.project.modules += tasks.collect { it.module }
		project.tasks["idea"].dependsOn(tasks)
	}

	JavaExec addExecTask(Project project, WrapperConfigExtension wrapper, Locality locality) {
		project.tasks.create("run$wrapper.name$locality", JavaExec).doFirst { JavaExec task ->
			WrapperManager
				.getLaunch(project, wrapper, locality)
				.configureJavaExec(project, task)
		}.dependsOn(project.tasks["jar"]) as JavaExec
	}

	GenerateIdeaModule addIdeaModuleTask(Project project, WrapperConfigExtension wrapper, Locality locality) {
		def instancePath = project.rootDir.toPath().resolve("run/$wrapper.name/$locality/")

		def config = project.configurations.maybeCreate("$wrapper.name-$locality-runtime-idea").extendsFrom(project.configurations["runtime"])
		project.dependencies.add(config.name, project)

		def idea = project.tasks.create("create${wrapper.name}${locality}Module", GenerateIdeaModule)

		idea.module = new IdeaModule(project, new IdeaModuleIml(new XmlTransformer(), instancePath.toFile()))
		idea.module.with {
			pathFactory = new PathFactory()
			contentRoot = instancePath.toFile()
			name = "$wrapper.name-$locality"
			scopes += [RUNTIME: [plus: [config]]]
		}

		idea.doFirst {
			def launch = WrapperManager.getLaunch(project, wrapper, locality)
			project.dependencies.add(config.name, project.files(launch.extraClasspath))
		}

		idea
	}

	def generateIdeaRunconfig(Element project, WrapperConfigExtension extension, Locality locality) {
		project.getElementsByTagName("component").find { it.getAttribute(a) }
	}
}
