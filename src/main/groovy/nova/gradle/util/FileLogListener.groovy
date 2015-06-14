package nova.gradle.util

import groovy.transform.CompileStatic
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.StandardOutputListener

@CompileStatic
class FileLogListener implements StandardOutputListener, BuildListener {
	private BufferedWriter out

	FileLogListener(File out) {
		if (out.exists()) {
			out.delete()
		} else {
			out.getParentFile().mkdirs()
		}

		this.out = out.newWriter()
	}

	@Override
	void onOutput(CharSequence output) {
		out << output
	}

	@Override
	void buildFinished(BuildResult result) {
		out.close()
	}

	@Override
	void buildStarted(Gradle gradle) {} //noop

	@Override
	void settingsEvaluated(Settings settings) {} //noop

	@Override
	void projectsLoaded(Gradle gradle) {} //noop

	@Override
	void projectsEvaluated(Gradle gradle) {} //noop
}
