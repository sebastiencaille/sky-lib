package ch.skymarshall.dataflowmgr.generator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Dto {
	public UUID uuid;
	public String name;
	public String description;
	public Map<String, String> fields = new HashMap<>();
}
