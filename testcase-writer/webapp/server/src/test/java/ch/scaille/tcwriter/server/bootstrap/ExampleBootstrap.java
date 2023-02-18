package ch.scaille.tcwriter.server.bootstrap;

import ch.scaille.tcwriter.examples.ExampleHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExampleBootstrap {

    private static final Path SRV_DATA = Paths.get("/var/lib/tcwriter/data");

    public static void main(String[] args) throws IOException {
        final var exampleHelper = new ExampleHelper(SRV_DATA);
        final var model = exampleHelper.generateDictionary();
        final var tc = exampleHelper.recordTestCase(model);

        exampleHelper.getModelDao().writeTestDictionary(model);
        exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);
    }
}
