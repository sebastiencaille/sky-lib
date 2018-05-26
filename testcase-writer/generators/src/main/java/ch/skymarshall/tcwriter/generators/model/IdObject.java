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

}
