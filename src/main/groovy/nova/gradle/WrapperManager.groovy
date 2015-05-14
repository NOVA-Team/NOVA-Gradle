package nova.gradle

import org.gradle.api.tasks.JavaExec

/**
 * @author rx14
 */
class WrapperManager {
	public static final Map<String, Wrapper> wrappers = new HashMap<>()

	static void add(Wrapper w) {
		wrappers.put(w.name, w)
	}

	static JavaExec get(String wrapper, Locality locality, Map<String, String> options) {
		Wrapper w = wrappers.get(wrapper)

		w.getTask(locality, options)
	}
}
