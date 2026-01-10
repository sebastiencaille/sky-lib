package ch.scaille.tcwriter.model;

import lombok.Getter;

@Getter
public class IdObject {

	public static final String ID_NOT_SET = "NotSet";

	private final String id;

	public IdObject(final String id) {
		this.id = id;
	}

    @Override
	public String toString() {
		return super.toString() + ": " + id;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && id.equals(((IdObject) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
