package ch.skymarshall.generators.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CodeGeneratorParams {

	@Parameter(names = { "-s", "--sourceFolder" }, required = true)
	private String sourceFolder = ".";

	@Parameter(names = { "-t", "--targetFolder" }, required = false)
	private String targetFolder = null;

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
		if (targetFolder == null) {
			// by default, store in source folder so changes can be audited
			return sourceFolder; 
		}
		return targetFolder;
	}

	public String getNamespaceFilter() {
		return namespaceFilter;
	}
}
