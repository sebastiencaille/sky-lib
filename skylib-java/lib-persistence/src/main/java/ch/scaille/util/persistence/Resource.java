package ch.scaille.util.persistence;

import lombok.Getter;

/**
 * A resource (meta data + data)
 * 
 * @param <T>
 */
@Getter
public class Resource<T> extends ResourceMetaData {

	private final T value;

	public Resource(String identifier, String storage, String mimeType, T data) {
		super(identifier, storage, mimeType);
		this.value = data;
	}

	public Resource(Resource<?> resource, T value) {
		super(resource.getIdentifier(), resource.getStorageLocator(), resource.getMimeType());
		this.value = value;
	}

}
