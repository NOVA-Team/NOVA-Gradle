package nova.gradle

import groovy.transform.CompileStatic
import nova.gradle.extensions.WrapperConfigExtension
import org.gradle.api.Project

import java.nio.file.Path

@CompileStatic
interface Wrapper {
	boolean canHandle(WrapperConfigExtension extension, Locality locality);

	JavaLaunchContainer getLaunch(Project project, WrapperConfigExtension extension, Locality locality, Path instancePath);
}
