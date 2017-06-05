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
	void registerObject(IDData data, String simpleName);

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
