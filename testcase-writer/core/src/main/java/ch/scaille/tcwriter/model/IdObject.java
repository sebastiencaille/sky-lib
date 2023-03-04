package ch.scaille.tcwriter.model;

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
	public String toString() {
		return super.toString() + ": " + id;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IdObject idObj && id.equals(idObj.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
