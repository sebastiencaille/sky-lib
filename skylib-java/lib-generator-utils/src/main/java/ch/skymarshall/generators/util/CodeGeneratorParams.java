package ch.skymarshall.generators.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CodeGeneratorParams {

	@Parameter(names = { "-s", "--sourceFolder" }, required = true)
	private String sourceFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" }, required = true)
	private String targetFolder = "src-generated";

	@Parameter(names = { "-ns", "--nameSpace" }, required = true)
	private String namespaceFilter = null;

	public static CodeGeneratorParams parse(final String[] args) {
		final CodeGeneratorParams params = new CodeGeneratorParams();
		JCommander.newBuilder().addObject(params).build().parse(args);
		return params;
	}

	public String getSourceFolder() {
		return sourceFolder;
	}

	public String getTargetFolder() {
		return targetFolder;
	}

	public String getNamespaceFilter() {
		return namespaceFilter;
	}
}
