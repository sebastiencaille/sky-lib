package ch.scaille.util.persistence;

public class Resource<T> extends ResourceMeta {

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
		super(resource.getLocator(), resource.getStorage(), resource.getMimeType());
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
