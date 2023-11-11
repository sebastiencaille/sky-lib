package ch.scaille.dataflowmgr.model;

import java.util.UUID;

public class WithId {

	private final UUID uuid;

	public WithId(final UUID uuid) {
		this.uuid = uuid;
	}

	public UUID uuid() {
		return uuid;
	}

	public static UUID id() {
		return UUID.randomUUID();
	}
}
