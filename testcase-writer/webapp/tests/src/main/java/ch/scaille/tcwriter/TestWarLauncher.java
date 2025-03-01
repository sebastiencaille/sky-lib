package ch.scaille.tcwriter;

import java.net.URL;
import java.util.Collection;

import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.launch.LaunchedClassLoader;
import org.springframework.boot.loader.launch.WarLauncher;

public class TestWarLauncher extends WarLauncher {
    
    public TestWarLauncher() throws Exception {
        super();
    }

    @Override
    protected String getMainClass() throws Exception {
        return "ch.scaille.tcwriter.server.Server";
    }

    @Override
    protected boolean isIncludedOnClassPathAndNotIndexed(Archive.Entry entry) {
        return true;
    }

    @Override
    protected ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
        return new LaunchedClassLoader(isExploded(), getArchive(), urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
    }

    public static void main(String[] args) throws Exception {
        new TestWarLauncher().launch(args);
    }
}
