package ch.scaille.util.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

/**
 * Handles resources located in the classpath
 * 
 * @param <T>
 */
public class ModuleDao<T> extends ClassPathDao<T> {

	private final Module[] modules;

	public ModuleDao(Class<T> daoType, Path resourcePath, StorageDataHandlerRegistry serDeserializerRegistry, Set<String> whiteList, Module... modules) {
		super(daoType, resourcePath, serDeserializerRegistry, whiteList);
		this.modules = modules;
	}

	@Override
	protected Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException {
		for (var module: modules) {
			try (var resStream = module
					.getResourceAsStream(resourceMetaData.getStorageLocator())) {
				if (resStream != null) {
					return resourceMetaData.withValue(JavaExt.readUTF8Stream(resStream));
				}
			}
		}
		throw new IOException("Resource not found: " + resourceMetaData);
	}

}
