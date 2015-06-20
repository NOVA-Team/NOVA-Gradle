package nova.gradle.util

import groovy.transform.CompileStatic
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord

@CompileStatic
class GradleLogHandler extends Handler {

	static final Map<Level, LogLevel> infoMappings = [
		(Level.ALL): LogLevel.DEBUG,
		(Level.FINEST): LogLevel.DEBUG,
		(Level.FINER): LogLevel.DEBUG,
		(Level.FINE): LogLevel.INFO,
		(Level.CONFIG): LogLevel.INFO,
		(Level.INFO): LogLevel.LIFECYCLE,
		(Level.WARNING): LogLevel.WARN,
		(Level.SEVERE): LogLevel.ERROR,
		(Level.OFF): null,
	] as Map<Level, LogLevel>

	final Logger gradleLogger
	final Map<Level, LogLevel> mappings

	GradleLogHandler(Logger gradleLogger, Map<Level, LogLevel> mappings) {
		this.gradleLogger = gradleLogger
		this.mappings = mappings
	}

	@Override
	void publish(LogRecord record) {
		def gradleLevel = mappings.get(record.level)
		if (gradleLevel) {
			gradleLogger.log(gradleLevel, record.message, record.thrown)
		}
	}

	@Override
	void flush() {

	}

	@Override
	void close() throws SecurityException {

	}
}
