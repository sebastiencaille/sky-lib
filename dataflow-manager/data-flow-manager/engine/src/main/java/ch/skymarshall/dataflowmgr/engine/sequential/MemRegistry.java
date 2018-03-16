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
package ch.skymarshall.dataflowmgr.engine.sequential;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import ch.skymarshall.dataflowmgr.model.IDData;
import ch.skymarshall.dataflowmgr.model.Registry;

public class MemRegistry implements Registry {

	private final Map<String, Object> data = new HashMap<>();
	private final Map<UUID, String> names = new HashMap<>();

	@Override
	public void registerObject(final IDData data, final String name) {
		names.put(data.uuid(), name);
	}

	@Override
	public String getNameOf(final UUID uuid) {
		return names.get(uuid);
	}

	@Override
	public <T> T get(final UUID uuid) {
		return null;
	}

	@Override
	public <T> T get(final UUID apUuid, final UUID flowUuid, final Supplier<T> newData) {
		final String id = apUuid + "-" + flowUuid;
		return (T) data.computeIfAbsent(id, k -> newData.get());
	}

}
