package nova.gradle.wrappers

import nova.gradle.JavaLaunchContainer
import nova.gradle.Locality
import nova.gradle.Wrapper
import nova.gradle.extensions.WrapperConfigExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.logging.LogLevel
import uk.co.rx14.jmclaunchlib.MCInstance
import uk.co.rx14.jmclaunchlib.util.NullSupplier

import java.nio.file.Path

class MinecraftWrapper implements Wrapper {

	def wrappers = [
		"NovaWrapper-MC1.7.10": ["1.7.10-10.13.4.1448-1.7.10", "1.7.10", ["-Dfml.coreMods.load=nova.wrapper.mc1710.NovaMinecraftCore"]].asImmutable(),
		"NovaWrapper-MC1.8": ["1.8-11.14.3.1446", "1.8", ["-Dfml.coreMods.load=nova.wrapper.mc18.NovaMinecraftCore"]].asImmutable()

	].asImmutable()

	@Override
	boolean canHandle(WrapperConfigExtension extension, Locality locality) {
		extension.wrapper.startsWith("nova.wrapper.mc") && locality == Locality.Client
	}

	@Override
	JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension extension, Locality locality, Path instancePath) {
		project.logger.lifecycle "Creating instance..."

		def logLevelBefore = project.logging.level
		project.logging.level = LogLevel.INFO

		def config = project.configurations.maybeCreate("$extension.name-$locality-runtime")
		project.dependencies.with {
			add(config.name, module(extension.wrapper) {
				transitive = true
			})
		}

		def (String forgeVersion, String mcVersion, List<String> extraVMArgs) = wrappers[extension.wrapper.split(":")[1]]

		def instance = MCInstance.createForge(
			mcVersion,
			forgeVersion,
			instancePath,
			project.gradle.gradleUserHomeDir.toPath().resolve("caches/minecraft"),
			NullSupplier.INSTANCE
		)

		def spec = instance.getOfflineLaunchSpec("TestUser-${new Random().nextInt(100)}")

		project.logging.level = logLevelBefore

		new JavaLaunchContainer(
			extraClasspath: spec.classpath + config.resolve(),
			launchArgs: spec.launchArgs.toList(),
			jvmArgs: spec.jvmArgs.toList() + extraVMArgs,
			mainClass: spec.mainClass
		)
	}
}
