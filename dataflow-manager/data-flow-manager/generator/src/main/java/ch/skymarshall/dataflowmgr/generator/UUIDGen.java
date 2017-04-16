package ch.skymarshall.dataflowmgr.generator;

import java.util.UUID;

public class UUIDGen {
	public static void main(final String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(UUID.randomUUID());
		}
	}
}
