package ch.scaille.tcwriter.server.config;

import java.io.IOException;
import java.nio.file.Paths;

public class SetupDemoServer {
    public static void main(String[] args) throws IOException {
        new BootstrapConfig(Paths.get(System.getProperty("user.home")).resolve(".var/lib/tcwriter/data")).bootStrapDemo();
    }
}
