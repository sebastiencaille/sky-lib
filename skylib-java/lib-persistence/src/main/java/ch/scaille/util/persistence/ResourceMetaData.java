package ch.scaille.util.persistence;

/**
 * A full reference to a persistent data
 */
public class ResourceMetaData {

	/**
	 * The abstract locator of the resource
	 */
	private final String locator;
	/**
	 * The storage specific location
	 */
	private final String storageLocator;
	/**
	 * the resource mime type
	 */
	private final String mimeType;

	public ResourceMetaData(String locator, String storageLocator, String mimeType) {
		this.locator = locator;
		this.storageLocator = storageLocator;
		this.mimeType = mimeType;
	}

	public String getLocator() {
		return locator;
	}

	public String getStorageLocator() {
		return storageLocator;
	}

	public String getMimeType() {
		return mimeType;
	}

	public ResourceMetaData withStorageLocator(String newStorageLocator) {
		return new ResourceMetaData(getLocator(), newStorageLocator, getMimeType());
	}

	public <U> Resource<U> withValue(U value) {
		return new Resource<>(locator, storageLocator, mimeType, value);
	}

	@Override
	public String toString() {
		return locator + ',' + storageLocator + ',' + mimeType;
	}
}
