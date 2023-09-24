package ch.scaille.generators.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.util.helpers.JavaExt;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeGeneratorParams {

	@Parameter(names = { "-s", "--sourceFolder" }, required = true)
	private String sourceFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" })
	private String targetFolder = null;

	@Parameter(names = { "-ns", "--nameSpace" }, required = true)
	private String namespaceFilter = null;

	public static CodeGeneratorParams parse(final String[] args) {
		final var params = new CodeGeneratorParams();
		JCommander.newBuilder().addObject(params).build().parse(args);
		return params;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public String getTargetFolder() {
		if (targetFolder == null) {
			// by default, store in source folder so changes can be audited
			return sourceFolder;
		}
		return targetFolder;
	}

	public String getNamespaceFilter() {
		return namespaceFilter;
	}

	/**
	 * Gets the folder of a class
	 * @param clazz
	 * @return
	 */
    public static Path mavenTarget(Class<?> clazz) {
         return Paths.get(JavaExt.pathOf(clazz.getProtectionDomain().getCodeSource().getLocation())).resolve("..");
    }

}
