package ch.skymarshall.util.dao.metadata;

public class MetadataHelper {
	public static String toFirstLetterInUpperCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String toFirstLetterInLowerCase(final String str) {
		if (str == null) {
			return null;
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

}
