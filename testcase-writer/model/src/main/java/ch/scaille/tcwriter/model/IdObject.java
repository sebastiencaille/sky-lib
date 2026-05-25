package ch.scaille.tcwriter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
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

}
