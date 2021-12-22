package ch.scaille.tcwriter.generators;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;

public class GeneratorConfig {

	private String name = "default";

	private String dictionaryPath;

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
	public String getDictionaryPath() {
		return dictionaryPath;
	}

	public void setDictionaryPath(final String dictionaryPath) {
		this.dictionaryPath = dictionaryPath;
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
