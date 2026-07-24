package ch.scaille.generators.util;

import java.nio.file.Path;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.util.helpers.JavaExt;
import lombok.Getter;


public class CodeGeneratorParams implements ICodeGeneratorParams{

	@Getter
    @Parameter(names = { "-cp", "--cpFolder" }, required = true)
	private String classPathFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" })
	@Nullable
	private String targetFolder = null;

	@Getter
    @Parameter(names = { "-sp", "--scanPackage" }, required = true)
	private String scanPackage = null;

	@Getter
    @Parameter(names = { "-tp", "--targetPackage" })
	@Nullable
	private String targetPackage = null;

	public static CodeGeneratorParams parse(final String[] args) {
		final var params = new CodeGeneratorParams();
		JCommander.newBuilder().addObject(params).build().parse(args);
		return params;
	}

	@Override
    public String getTargetFolder() {
        // by default, store the file in the source folder so changes can be audited
        return Objects.requireNonNullElse(targetFolder, classPathFolder);
    }

	public static Path mavenTargetFolderOf(Class<?> clazz) {
		return JavaExt.locationOf(clazz).resolve("..");
	}
}
