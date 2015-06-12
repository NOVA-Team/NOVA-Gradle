package nova.gradle.extensions

import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class WrapperConfigExtension {
	final String name

	public WrapperConfigExtension(String name) {
		this.name = name
	}

	String wrapper

	def wrapper(String wrapper) {
		this.wrapper = wrapper
	}
}
