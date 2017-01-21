package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;
import java.util.function.Supplier;

public interface Registry {

	void registerObject(IDData data, String simpleName);

	String getNameOf(UUID uuid);

	<T> T get(UUID flowUuid);

	<T> T get(final UUID flowUuid, final Supplier<T> newData);

}
