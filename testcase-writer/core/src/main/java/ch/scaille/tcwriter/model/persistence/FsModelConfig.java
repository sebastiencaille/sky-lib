package ch.scaille.tcwriter.model.persistence;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;

/**
 * File based configuration
 * @author scaille
 *
 */
public class FsModelConfig {

	public static final String DEFAULT = "default";
	
    private String name = DEFAULT;

    private String dictionaryPath;

    private String tcPath;

    private String templatePath;

    private String tcExportPath;

    public void setBase(String base) {
        dictionaryPath = base + "/dictionary";
        tcPath = base + "/testcase";
        templatePath = base + "/templates/TC.template";
        tcExportPath = base + "/exported";
    }

    public FsModelConfig() {
        setBase("${user.home}/.tcwriter/" + name);
    }

    @Ordered(order = 1)
    @Labeled(label = "Name of the configuration")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Ordered(order = 2)
    @Labeled(label = "Location of the models")
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
    public String getTcExportPath() {
        return tcExportPath;
    }

    public void setTcExportPath(final String tcExportPath) {
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
