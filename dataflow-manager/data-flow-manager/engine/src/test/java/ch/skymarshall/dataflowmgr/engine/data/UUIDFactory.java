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
package ch.skymarshall.dataflowmgr.engine.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDFactory {
	private static Map<Class<?>, UUID> classToUUID = new HashMap<>();

	public static UUID uuid() {
		return UUID.randomUUID();
	}

	public static UUID newUuid(final Class<?> clazz) {
		UUID uuid = classToUUID.get(clazz);
		if (uuid == null) {
			uuid = UUID.randomUUID();
			classToUUID.put(clazz, uuid);
		}
		return uuid;
	}
}
