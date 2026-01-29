package ch.scaille.tcwriter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode
public class IdObject {

	public static final String ID_NOT_SET = "NotSet";

	private final String id;

	public IdObject(final String id) {
		this.id = Objects.requireNonNull(id, "Id must not be null");
	}

    @Override
	public String toString() {
		return super.toString() + ": " + id;
	}

}
