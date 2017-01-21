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
