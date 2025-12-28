package ch.scaille.example.util.dao.metadata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADataObject {

	public static final String AN_ATTRIBUTE = "AnAttribute";

	private static final String READ_ONLY_ATTRIBUTE = "ReadOnlyData";

	private String anAttribute = "data1";

	public String getAReadOnlyAttribute() {
		return READ_ONLY_ATTRIBUTE;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
