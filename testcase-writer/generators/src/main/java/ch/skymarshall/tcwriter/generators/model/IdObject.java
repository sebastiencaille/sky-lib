package ch.skymarshall.tcwriter.generators.model;

public class IdObject {

	public static final String ID_NOT_SET = "NotSet";

	private final String id;

	public IdObject(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IdObject && id.equals(((IdObject) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
