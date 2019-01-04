/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator;

public abstract class Utils {
	private Utils() {
	}

	public static String toCamelCase(final String str) {
		final StringBuilder result = new StringBuilder();

		final String[] parts = str.split("_");
		for (final String part : parts) {
			result.append(Character.toUpperCase(part.charAt(0)));
			if (part.length() > 1) {
				result.append(part.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	public static String firstUpperCase(final String str) {
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		final char firstUpper = Character.toUpperCase(str.charAt(0));
		return firstUpper + str.substring(1);
	}

	public static String firstLowerCase(final String str) {
		if (str.length() == 1) {
			return str.toLowerCase();
		}
		final char firstUpper = Character.toLowerCase(str.charAt(0));
		return firstUpper + str.substring(1);
	}

}
