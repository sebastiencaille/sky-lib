package ch.scaille.util.persistence;

/**
 * A resource (meta data + data)
 * 
 * @param <T>
 */
public class Resource<T> extends ResourceMetaData {

	private final T value;

	public Resource(String locator, String storage, String mimeType) {
		super(locator, storage, mimeType);
		this.value = null;
	}

	public Resource(String locator, String storage, String mimeType, T data) {
		super(locator, storage, mimeType);
		this.value = data;
	}

	public Resource(Resource<?> resource, T value) {
		super(resource.getLocator(), resource.getStorageLocator(), resource.getMimeType());
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
