package ch.scaille.util.persistence;

public class Resource {

	private final String name;
	private final String storage;
	private final String mimeType;
	private final String data;

	public Resource(String name, String storage, String mimeType, String data) {
		this.name = name;
		this.storage = storage;
		this.mimeType = mimeType;
		this.data = data;
	}

	public Resource(Resource resource, String data) {
		this.name = resource.getName();
		this.storage = resource.getStorage();
		this.mimeType = resource.getMimeType();
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public String getStorage() {
		return storage;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getData() {
		return data;
	}

	public Resource fixStorage(String fixedStorage) {
		return new Resource(name, fixedStorage, mimeType, data);
	}
	
	@Override
	public String toString() {
		return name + ' ' + storage + ' ' + mimeType;
	}
	
}
