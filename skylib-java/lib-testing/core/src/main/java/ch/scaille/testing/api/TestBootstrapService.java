package ch.scaille.testing.api;

import java.nio.file.Path;

public interface TestBootstrapService {

	void bootstrap(Path dataFolder);
	
}
