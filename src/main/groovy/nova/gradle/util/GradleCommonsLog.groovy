package nova.gradle.util

import org.apache.commons.logging.Log
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

import static org.gradle.api.logging.LogLevel.*

class GradleCommonsLog implements Log {

	Logger logger
	String name

	@Override
	boolean isDebugEnabled() { true	}

	@Override
	boolean isErrorEnabled() { true	}

	@Override
	boolean isFatalEnabled() { true	}

	@Override
	boolean isInfoEnabled() { true }

	@Override
	boolean isTraceEnabled() { true	}

	@Override
	boolean isWarnEnabled() { true }

	void log(LogLevel level, Object o, Throwable throwable) {
		logger.log(level, String.valueOf(o), throwable)
	}

	void log(LogLevel level, Object o) {
		logger.log(level, String.valueOf(o))
	}

	@Override
	void trace(Object o) {
		log(DEBUG, o)
	}

	@Override
	void trace(Object o, Throwable t) {
		log(DEBUG, o, t)
	}

	@Override
	void debug(Object o) {
		log(INFO, o)
	}

	@Override
	void debug(Object o, Throwable t) {
		log(INFO, o, t)
	}

	@Override
	void info(Object o) {
		log(LIFECYCLE, o)
	}

	@Override
	void info(Object o, Throwable t) {
		log(LIFECYCLE, o ,t)
	}

	@Override
	void warn(Object o) {
		log(WARN, o)
	}

	@Override
	void warn(Object o, Throwable t) {
		log(WARN, o, t)
	}

	@Override
	void error(Object o) {
		log(ERROR, o)
	}

	@Override
	void error(Object o, Throwable t) {
		log(ERROR, o, t)
	}

	@Override
	void fatal(Object o) {
		log(ERROR, o)
	}

	@Override
	void fatal(Object o, Throwable t) {
		log(ERROR, o, t)
	}
}
