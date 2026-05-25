package ch.scaille.util.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.Nullable;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public abstract class AbstractFSSerializationDao<T> extends AbstractSerializationDao<T> {

    private static final String[] EMPTY_NAME_EXT = new String[] { "", "" };

    private static final Path PATH_VALIDATING_BASE = Paths.get("/", "MyBasePath");
    
	private final boolean validatePath;
	    
    protected AbstractFSSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry, boolean validatePath) {
        super(daoType, serDeserializerRegistry);
        this.validatePath = validatePath;
    }

    @Override
    protected String extensionOf(String storageLocator) {
        return nameAndExtensionOf(storageLocator)[1];
    }

    @Override
    protected ResourceMetaData fixOrValidate(ResourceMetaData resourceMetaData) {
        if (!extensionOf(resourceMetaData.getStorageLocator()).isEmpty()) {
            return resourceMetaData;
        }
        final var mimetypeHandler = dataHandlerRegistry.getHandlerOf(resourceMetaData.getMimeType());
        return resourceMetaData.withStorageLocator(resourceMetaData.getStorageLocator() + '.' + mimetypeHandler.getDefaultExtension());
    }

    /**
     * Find the name an extension of the locator
     * @param storageLocator a path
     * @return an array containing the path without extension and the extension 
     */
    protected static String[] nameAndExtensionOf(@Nullable String storageLocator) {
        if (storageLocator == null) {
            return EMPTY_NAME_EXT;
        }
        final var path = Paths.get(storageLocator).getFileName().toString();
        final var lastDot = path.lastIndexOf('.');
        final String[] nameAndExt;
        if (lastDot >= 0) {
            final var extension = path.substring(lastDot + 1);
            nameAndExt = new String[] { storageLocator.substring(0, storageLocator.length() - extension.length() - 1), extension};
        } else {
            nameAndExt = new String[] { storageLocator, "" };
        }
        return nameAndExt;
    }
    

	protected Path validateLocator(Path basePath, Path locator) {
		if (!validatePath) {
			return locator;
		}
		final var validatedPath = locator.toAbsolutePath();
		if (!validatedPath.startsWith(basePath.toAbsolutePath())) {
			throw new IllegalStateException("Locator must be within base path: " + validatedPath + " not in " + basePath);
		}
		return locator;
	}
    
	protected String validateIdentifier(String identifier) {
		validateLocator(PATH_VALIDATING_BASE, PATH_VALIDATING_BASE.resolve(identifier));
		return identifier;
	}
    
}
