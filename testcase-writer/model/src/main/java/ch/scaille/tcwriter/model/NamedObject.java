package ch.scaille.tcwriter.model;

import lombok.Getter;

@Getter
public class NamedObject extends IdObject {

	private String name;

	public NamedObject(final String id, final String name) {
		super(id);
		this.name = name;
	}

    protected void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
