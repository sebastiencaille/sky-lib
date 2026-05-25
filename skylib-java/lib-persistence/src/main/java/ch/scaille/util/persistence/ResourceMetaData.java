package ch.scaille.util.persistence;

import lombok.Getter;

/**
 * A full reference to a persistent data
 */
@Getter
public class ResourceMetaData {

	/**
	 * The abstract locator of the resource
	 */
	private final String identifier;
	/**
	 * The storage specific location
	 */
	private final String storageLocator;
	/**
	 * the resource mime type
	 */
	private final String mimeType;

	public ResourceMetaData(String identifier, String storageLocator, String mimeType) {
		this.identifier = identifier;
		this.storageLocator = storageLocator;
		this.mimeType = mimeType;
	}

    public ResourceMetaData withStorageLocator(String newStorageLocator) {
		return new ResourceMetaData(getIdentifier(), newStorageLocator, getMimeType());
	}

	public ResourceMetaData withMimeType(String newMimetype) {
		return new ResourceMetaData(getIdentifier(), getStorageLocator(), newMimetype);
	}
    
	public <U> Resource<U> withValue(U value) {
		return new Resource<>(identifier, storageLocator, mimeType, value);
	}

	@Override
	public String toString() {
		return identifier + ',' + storageLocator + ',' + mimeType;
	}

}
