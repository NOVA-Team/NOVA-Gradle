package nova.gradle.wrappers

import nova.gradle.JavaLaunchContainer
import nova.gradle.Locality
import nova.gradle.Wrapper
import nova.gradle.extensions.WrapperConfigExtension
import nova.gradle.util.GradleCommonsLog
import org.apache.commons.logging.Log
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.Logging
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import uk.co.rx14.jmclaunchlib.LaunchTaskBuilder

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

import static org.reflections.ReflectionUtils.*

class MinecraftWrapper implements Wrapper {

	def wrappers = [
		"NOVA-Core-Wrapper-MC1.7": ["1.7.10-10.13.4.1448-1.7.10", "1.7.10"],
		"NOVA-Core-Wrapper-MC1.8"   : ["1.8-11.14.3.1446", "1.8"]
	]

	@Override
	boolean canHandle(WrapperConfigExtension extension, Locality locality) {
		wrappers.keySet().contains(extension.wrapper.split(":")[1]) && locality == Locality.Client
	}

	@Override
	JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension extension, Locality locality, Path instancePath, Configuration config) {

		setLogging()

		//TODO: put forge and MC version in META-INF on the wrapper
		def wrapperName = extension.wrapper.split(":")[1]

		installWrapper(project, wrapperName, extension, instancePath)

		def (String forgeVersion, String mcVersion) = wrappers[wrapperName]

		def task = new LaunchTaskBuilder()
			.setNetOffline(project.gradle.startParameter.offline)
			.setCachesDir(project.gradle.gradleUserHomeDir.toPath().resolve("caches/minecraft"))
			.setInstanceDir(instancePath)
			.setForgeVersion(mcVersion, forgeVersion)
			.setOffline()
			.setUsername("TestUser-${new Random().nextInt(100)}")
			.build()

		def spec = task.spec

		new JavaLaunchContainer(
			extraClasspath: spec.classpath,
			launchArgs: spec.launchArgs.toList(),
			jvmArgs: spec.jvmArgs.toList(),
			mainClass: spec.mainClass
		)
	}

	private void installWrapper(Project project, String wrapperName, WrapperConfigExtension extension, Path instancePath) {
		//Resolve wrapper dependency
		def wrapperConfig = project.configurations.maybeCreate("novawrapper-$wrapperName")
		project.dependencies.with {
			add(wrapperConfig.name, module(extension.wrapper) {
				transitive = true
			})
		}

		def files = wrapperConfig.resolve()
		assert files.size() == 1

		//Hacks ensue: put the wrapper in the mdos folder
		def wrapperFile = instancePath.resolve("mods/NovaWrapper.jar")
		wrapperFile.toFile().parentFile.mkdirs()
		Files.copy(files[0].toPath(), wrapperFile, StandardCopyOption.REPLACE_EXISTING)
	}

	private void setLogging() {
		new Reflections("uk.co.rx14.jmclaunchlib", new SubTypesScanner(false))
			.getSubTypesOf(Object)
			.collect { getAllFields(it, withType(Log), withModifier(Modifier.STATIC)) }
			.flatten()
			.each { Field field ->
				field.setAccessible(true)

				def modifiers = field.class.getDeclaredField("modifiers")
				modifiers.setAccessible(true)
				modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL)

				field.set(null, (Log) new GradleCommonsLog(logger: Logging.getLogger(field.class)))
			}
	}

}
