package ch.scaille.testing.spring;

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
        final var mainClass = System.getProperty("mainClass");
        if (mainClass == null) {
        	throw new IllegalStateException("System Property mainClass must be defined");
        }
		return mainClass;
    }

    @Override
    protected boolean isIncludedOnClassPathAndNotIndexed(Archive.Entry entry) {
        return true;
    }

    @Override
    protected ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
        try (var archive = getArchive()) {
        	return new LaunchedClassLoader(isExploded(), archive, urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
        }
    }

    public static void main(String[] args) throws Exception {
        new TestWarLauncher().launch(args);
    }
}
