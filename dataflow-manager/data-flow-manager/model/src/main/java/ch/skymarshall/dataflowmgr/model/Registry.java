package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;
import java.util.function.Supplier;

public interface Registry {

	void registerObject(IDData data, String simpleName);

	void register(UUID uuid, Object inputData);

	<T> T get(UUID uuid);

	<T> T get(final UUID uuid, final Supplier<T> newData);

	String getNameOf(UUID uuid);

}
