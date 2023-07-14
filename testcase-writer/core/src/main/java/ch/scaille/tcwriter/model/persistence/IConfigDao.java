package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.util.function.Consumer;

import ch.scaille.tcwriter.config.TCConfig;

public interface IConfigDao {

    void onReload(Consumer<TCConfig> hook);

    TCConfig getCurrentConfig();

    IResourceRepository loaderOf(String subPath, String extensions);

	void saveConfiguration() throws IOException;
}
