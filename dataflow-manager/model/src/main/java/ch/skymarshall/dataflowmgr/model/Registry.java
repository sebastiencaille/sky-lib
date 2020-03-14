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
package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;
import java.util.function.Supplier;

public interface Registry {

	/**
	 * Register an object so we can convert it's id to a user readable string
	 *
	 * @param data
	 * @param simpleName
	 */
	void registerObject(WithId data, String simpleName);

	/**
	 * Get the user readable name matching the uuid
	 *
	 * @param uuid
	 * @return
	 */
	String getNameOf(UUID uuid);

	<T> T get(UUID flowUuid);

	<T> T get(UUID actionPointUUID, UUID flowUuid, Supplier<T> newData);

}
