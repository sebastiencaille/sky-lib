package ch.scaille.tcwriter.config;

import java.util.function.Consumer;

public interface IConfigManager {

    void onReload(Consumer<TCConfig> hook);

    TCConfig getCurrentConfig();

    IResourceLoader loaderOf(String subPath, String extensions);
}
