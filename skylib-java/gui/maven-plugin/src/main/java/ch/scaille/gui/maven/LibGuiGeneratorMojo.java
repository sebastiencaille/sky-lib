package ch.scaille.gui.maven;


import ch.scaille.generators.util.ICodeGeneratorParams;
import ch.scaille.gui.mvc.GuiModelGenerator;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.jspecify.annotations.NullMarked;

import ch.scaille.generators.util.GenerationMetadata;

import lombok.SneakyThrows;

@Mojo(name = "generateMvc", defaultPhase = "GENERATE_TEST_SOURCES")
@NullMarked
public class LibGuiGeneratorMojo implements org.apache.maven.api.plugin.Mojo {

    @Parameter(property = "classPathFolder", required = true)
    private String classPathFolder = "";

    @Parameter(property = "scanPackage", required = true)
    private String scanPackage = "";

    @Parameter(property = "targetPackage")
    private String targetPackage = "";

    @Parameter(property = "targetFolder", required = true)
    private String targetFolder = "";

    @SneakyThrows
    @Override
    public void execute() {

        final var generationMetadata = new GenerationMetadata(LibGuiGeneratorMojo.class,"maven plugin");
        final var params = new ICodeGeneratorParams() {

            @Override
            public String getClassPathFolder() {
                return classPathFolder;
            }

            @Override
            public String getScanPackage() {
                return scanPackage;
            }

            @Override
            public String getTargetPackage() {
                return targetPackage;
            }

            @Override
            public String getTargetFolder() {
                return targetFolder;
            }
        };
        new GuiModelGenerator().process(params, generationMetadata);
    }

}
