package ch.skymarshall.tcwriter.generators;

import ch.skymarshall.annotations.Labeled;
import ch.skymarshall.annotations.Ordered;

public class GeneratorConfig {

	private String name = "default";

	private String modelPath;

	private String tcPath;

	private String defaultGeneratedTCPath;

	private String templatePath;

	@Ordered(order = 1)
	@Labeled(label = "Name of the configuration")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Ordered(order = 2)
	@Labeled(label = "Location of the model")
	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(final String modelPath) {
		this.modelPath = modelPath;
	}

	@Ordered(order = 3)
	@Labeled(label = "Location of the test cases")
	public String getTcPath() {
		return tcPath;
	}

	public void setTcPath(final String tcPath) {
		this.tcPath = tcPath;
	}

	@Ordered(order = 4)
	@Labeled(label = "Location of the exported test cases")
	public String getDefaultGeneratedTCPath() {
		return defaultGeneratedTCPath;
	}

	public void setDefaultGeneratedTCPath(final String defaultGeneratedTCPath) {
		this.defaultGeneratedTCPath = defaultGeneratedTCPath;
	}

	@Ordered(order = 5)
	@Labeled(label = "Location of the test case template")
	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
	}

}
