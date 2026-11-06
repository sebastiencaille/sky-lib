package ch.scaille.gui.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jspecify.annotations.NullMarked;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.ICodeGeneratorParams;
import ch.scaille.gui.mvc.GuiModelGenerator;
import lombok.SneakyThrows;

@Mojo(name = "generateMvc", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
@NullMarked
public class LibGuiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

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
