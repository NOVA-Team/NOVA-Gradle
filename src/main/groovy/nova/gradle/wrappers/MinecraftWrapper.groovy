package nova.gradle.wrappers

import nova.gradle.JavaLaunchContainer
import nova.gradle.Locality
import nova.gradle.Wrapper
import nova.gradle.extensions.WrapperConfigExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import uk.co.rx14.jmclaunchlib.MCInstance
import uk.co.rx14.jmclaunchlib.util.NullSupplier

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class MinecraftWrapper implements Wrapper {

	def wrappers = [
		"NovaWrapper-MC1.7.10": ["1.7.10-10.13.4.1448-1.7.10", "1.7.10"],
		"NovaWrapper-MC1.8"   : ["1.8-11.14.3.1446", "1.8"]
	]

	@Override
	boolean canHandle(WrapperConfigExtension extension, Locality locality) {
		extension.wrapper.startsWith("nova.wrapper.mc") && locality == Locality.Client
	}

	@Override
	JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension extension, Locality locality, Path instancePath, Configuration config) {
		//Get hardcoded wrapper config
		//TODO: put forge and MC version in META_INF on the wrapper
		def wrapperName = extension.wrapper.split(":")[1]
		def (String forgeVersion, String mcVersion) = wrappers[wrapperName]

		//Create launch spec, this configures the Minecraft instance
		def instance = MCInstance.createForge(
			mcVersion,
			forgeVersion,
			instancePath,
			project.gradle.gradleUserHomeDir.toPath().resolve("caches/minecraft"), //Cache directory
			NullSupplier.INSTANCE //Authentication information supplier
		)

		def spec = instance.getOfflineLaunchSpec("TestUser-${new Random().nextInt(100)}")

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

		new JavaLaunchContainer(
			extraClasspath: spec.classpath,
			launchArgs: spec.launchArgs.toList(),
			jvmArgs: spec.jvmArgs.toList(),
			mainClass: spec.mainClass
		)
	}
}
