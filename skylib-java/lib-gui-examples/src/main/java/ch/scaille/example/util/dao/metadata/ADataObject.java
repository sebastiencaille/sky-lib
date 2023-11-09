package ch.scaille.example.util.dao.metadata;

public class ADataObject {

	public static final String AN_ATTRIBUTE = "AnAttribute";

	private static final String READ_ONLY_ATTRIBUTE = "ReadOnlyData";

	private String anAttribute = "data1";

	public String getAnAttribute() {
		return anAttribute;
	}

	public void setAnAttribute(final String anAttribute) {
		this.anAttribute = anAttribute;
	}

	public String getAReadOnlyAttribute() {
		return READ_ONLY_ATTRIBUTE;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
