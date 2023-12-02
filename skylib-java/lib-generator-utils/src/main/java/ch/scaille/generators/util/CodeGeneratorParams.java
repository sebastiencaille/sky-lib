package ch.scaille.generators.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.util.helpers.JavaExt;

public class CodeGeneratorParams {

	@Parameter(names = { "-s", "--sourceFolder" }, required = true)
	private String sourceFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" })
	private String targetFolder = null;

	@Parameter(names = { "-sp", "--scanPackage" }, required = true)
	private String scanPackage = null;

	@Parameter(names = { "-tp", "--targetPackage" })
	private String targetPackage = null;

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

	public String getScanPackage() {
		return scanPackage;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	/**
	 * Gets the folder of a class
	 * 
	 * @param clazz
	 * @return
	 */
	public static Path mavenTarget(Class<?> clazz) {
		return Paths.get(JavaExt.pathOf(clazz.getProtectionDomain().getCodeSource().getLocation())).resolve("..");
	}

}
