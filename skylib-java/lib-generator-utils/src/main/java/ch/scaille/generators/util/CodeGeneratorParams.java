package ch.scaille.generators.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.util.helpers.JavaExt;
import lombok.Getter;

public class CodeGeneratorParams {

	@Getter
    @Parameter(names = { "-cp", "--cpFolder" }, required = true)
	private String classPathFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" })
	private String targetFolder = null;

	@Getter
    @Parameter(names = { "-sp", "--scanPackage" }, required = true)
	private String scanPackage = null;

	@Getter
    @Parameter(names = { "-tp", "--targetPackage" })
	private String targetPackage = null;

	public static CodeGeneratorParams parse(final String[] args) {
		final var params = new CodeGeneratorParams();
		JCommander.newBuilder().addObject(params).build().parse(args);
		return params;
	}

    public String getTargetFolder() {
        // by default, store in source folder so changes can be audited
        return Objects.requireNonNullElse(targetFolder, classPathFolder);
    }

    /**
	 * Gets the folder of a class
	 *
     */
	public static Path locationOf(Class<?> clazz) {
		return Paths.get(JavaExt.pathOf(clazz.getProtectionDomain().getCodeSource().getLocation()));
	}

	public static Path mavenTargetFolderOf(Class<?> clazz) {
		return locationOf(clazz).resolve("..");
	}
}
