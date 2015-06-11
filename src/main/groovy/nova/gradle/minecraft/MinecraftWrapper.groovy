package nova.gradle.minecraft

import nova.gradle.Locality
import nova.gradle.Wrapper
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import uk.co.rx14.jmclaunchlib.MCInstance
import uk.co.rx14.jmclaunchlib.util.NullSupplier

import java.nio.file.FileSystems
import java.nio.file.Path

class MinecraftWrapper implements Wrapper {
	@Override
	String getName() {
		"MC"
	}

	@Override
	void addTask(Project project, String taskName, Locality locality, Map<String, String> options) {
		project.task(type: JavaExec, taskName).doFirst {
			project.logger.lifecycle "Creating instance..."
			def instancePath = FileSystems.default.getPath("run/MC/client")

			instancePath.toFile().mkdirs()

			def instance = MCInstance.createForge(
				"1.7.10",
				"1.7.10-10.13.2.1300-1.7.10",
				instancePath,
				project.gradle.gradleUserHomeDir.toPath().resolve("caches/minecraft"),
				NullSupplier.INSTANCE
			)

			def spec = instance.getOfflineLaunchSpec("TestUser-${new Random().nextInt(100)}")

			classpath = project.files(spec.classpath)
			args = spec.launchArgs.toList()
			main = spec.mainClass
			jvmArgs = spec.jvmArgs.toList()
		}
	}
}
