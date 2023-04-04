package ch.scaille.tcwriter.config;

import java.util.function.Consumer;

public interface IConfigManager {

    void onReload(Consumer<TCConfig> hook);

    TCConfig getCurrentConfig();

    IResourceLoader configure(String subPath, String extensions);
}
