package ch.skymarshall.dataflowmgr.model;

import java.util.UUID;

public class IDData {

	private final UUID uuid;

	public IDData(final UUID uuid) {
		this.uuid = uuid;
	}

	public UUID uuid() {
		return uuid;
	}
}
