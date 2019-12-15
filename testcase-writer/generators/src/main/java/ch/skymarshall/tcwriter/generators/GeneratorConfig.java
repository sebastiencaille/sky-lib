package ch.skymarshall.tcwriter.generators;

import ch.skymarshall.util.annotations.Ordered;

public class GeneratorConfig {

	private String name = "default";

	private String modelPath;

	private String tcPath;

	private String defaultGeneratedTCPath;

	private String templatePath;

	@Ordered(order = 1)
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(final String modelPath) {
		this.modelPath = modelPath;
	}

	public String getTcPath() {
		return tcPath;
	}

	public void setTcPath(final String tcPath) {
		this.tcPath = tcPath;
	}

	public String getDefaultGeneratedTCPath() {
		return defaultGeneratedTCPath;
	}

	public void setDefaultGeneratedTCPath(final String defaultGeneratedTCPath) {
		this.defaultGeneratedTCPath = defaultGeneratedTCPath;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

}
