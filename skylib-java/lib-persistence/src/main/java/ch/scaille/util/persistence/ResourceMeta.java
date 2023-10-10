package ch.scaille.util.persistence;

public class ResourceMeta {
	private final String locator;
	private final String storage;
	private final String mimeType;

	public ResourceMeta(String locator, String storage, String mimeType) {
		this.locator = locator;
		this.storage = storage;
		this.mimeType = mimeType;
	}

	public String getLocator() {
		return locator;
	}

	public String getStorage() {
		return storage;
	}

	public String getMimeType() {
		return mimeType;
	}

	public ResourceMeta withStorage(String newStorage) {
		return new ResourceMeta(getLocator(), newStorage, getMimeType());
	}
	
	public <U> Resource<U> withValue(U value) {
		return new Resource<>(locator, storage, mimeType, value);
	}

	@Override
	public String toString() {
		return locator + ',' + storage + ',' + mimeType;
	}
}
