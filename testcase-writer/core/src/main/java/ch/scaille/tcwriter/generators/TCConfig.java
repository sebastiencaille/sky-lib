package ch.scaille.tcwriter.generators;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;

public class TCConfig {

	private String name = "default";

	private String dictionaryPath = "${user.home}/.tcwriter/default/dictionary";

	private String tcPath = "${user.home}/.tcwriter/default/testcase";

	private String templatePath = "${user.home}/.tcwriter/default/TC.template";

	private String tcExportPath = "${user.home}/.tcwriter/default/exported";

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
	public String getTCExportPath() {
		return tcExportPath;
	}

	public void setTCExportPath(final String tcExportPath) {
		this.tcExportPath = tcExportPath;
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