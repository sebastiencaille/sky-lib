package ch.scaille.tcwriter.persistence;

import ch.scaille.annotations.Labeled;
import ch.scaille.annotations.Ordered;
import lombok.Setter;

/**
 * File based configuration
 * @author scaille
 *
 */
@Setter
public class ModelConfig {


    private String dictionaryPath;

    private String tcPath;

    private String templatePath;

    private String tcExportPath;

    @Ordered(order = 2)
    @Labeled(label = "Location of the models")
    public String getDictionaryPath() {
        return dictionaryPath;
    }

    @Ordered(order = 3)
    @Labeled(label = "Location of the test cases")
    public String getTcPath() {
        return tcPath;
    }

    @Ordered(order = 4)
    @Labeled(label = "Location of the exported test cases")
    public String getTcExportPath() {
        return tcExportPath;
    }

    @Ordered(order = 5)
    @Labeled(label = "Location of the test case template")
    public String getTemplatePath() {
        return templatePath;
    }

}
