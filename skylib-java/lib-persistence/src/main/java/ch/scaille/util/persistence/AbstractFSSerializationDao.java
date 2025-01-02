package ch.scaille.util.persistence;

import java.nio.file.Paths;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public abstract class AbstractFSSerializationDao<T> extends AbstractSerializationDao<T> {

    private static final String[] EMPTY_NAME_EXT = new String[] { "", "" };

    protected AbstractFSSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
        super(daoType, serDeserializerRegistry);
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
    protected String[] nameAndExtensionOf(String storageLocator) {
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
}
