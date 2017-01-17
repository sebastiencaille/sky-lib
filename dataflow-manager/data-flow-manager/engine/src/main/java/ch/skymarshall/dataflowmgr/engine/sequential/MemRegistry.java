package ch.skymarshall.dataflowmgr.engine.sequential;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import ch.skymarshall.dataflowmgr.model.IDData;
import ch.skymarshall.dataflowmgr.model.Registry;

public class MemRegistry implements Registry {

	private final Map<UUID, Object> data = new HashMap<>();
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
	public void register(final UUID uuid, final Object inputData) {
		data.put(uuid, inputData);
	}

	@Override
	public <T> T get(final UUID uuid) {
		return null;
	}

	@Override
	public <T> T get(final UUID uuid, final Supplier<T> newData) {
		T value = (T) data.get(uuid);
		if (value == null) {
			value = newData.get();
			data.put(uuid, value);
			return value;
		}
		return value;
	}

}
