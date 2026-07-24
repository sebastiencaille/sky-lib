package ch.scaille.util.dao.metadata;

import org.jspecify.annotations.Nullable;

public class MetadataHelper {

	private MetadataHelper() {
	}

	@Nullable
	public static String toFirstLetterInUpperCase(@Nullable final String str) {
		if (str == null) {
			return null;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	@Nullable
	public static String toFirstLetterInLowerCase(@Nullable final String str) {
		if (str == null) {
			return null;
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

}
