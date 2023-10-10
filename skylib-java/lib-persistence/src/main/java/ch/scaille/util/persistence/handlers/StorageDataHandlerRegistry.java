package ch.scaille.util.persistence.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.scaille.util.persistence.Resource;

public class StorageDataHandlerRegistry {

	private final List<IStorageDataHandler> handlers = new ArrayList<>();

	private final IStorageDataHandler defaultDataHandler;

	public StorageDataHandlerRegistry(IStorageDataHandler defaultDataHandler) {
		this.defaultDataHandler = defaultDataHandler;
		if (defaultDataHandler != null) {
			handlers.add(defaultDataHandler);
		}
	}

	public void register(IStorageDataHandler dataHandler) {
		handlers.add(dataHandler);
	}

	public Optional<IStorageDataHandler> find(String extensionOrMimeType) {
		return handlers.stream().filter(t -> t.supports(extensionOrMimeType)).findFirst();
	}

	public String getDefaultExtension() {
		if (defaultDataHandler == null) {
			return null;
		}
		return defaultDataHandler.getDefaultExtension();
	}

	public IStorageDataHandler getHandlerOf(String mimeType) {
		return find(mimeType).orElseThrow(() -> new IllegalStateException("Not handled: " + mimeType));
	}

	private <T> IStorageDataHandler findHandler(Resource<T> resource) {
		return find(resource.getMimeType()).orElseThrow(() -> new IllegalStateException("Not handled: " + resource));
	}

	public <T> Resource<String> encode(Resource<T> resource, Class<T> targetType) throws IOException {
		return resource.withValue(findHandler(resource).encode(targetType, resource.getValue()));
	}

	public <T> Resource<T> decode(Resource<String> resource, Class<T> targetType) throws IOException {
		return resource.withValue(findHandler(resource).decode(resource.getValue(), targetType));
	}

	public IStorageDataHandler getDefaultHandler() {
		return defaultDataHandler;
	}

	public String getDefaultMimeType() {
		return defaultDataHandler.getDefaultMimeType();
	}
}
