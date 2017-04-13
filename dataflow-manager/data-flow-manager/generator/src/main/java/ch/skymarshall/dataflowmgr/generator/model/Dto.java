package ch.skymarshall.dataflowmgr.generator.model;

import java.util.HashMap;
import java.util.Map;

public class Dto {
	public String name;
	public String description;
	public Map<String, String> fields = new HashMap<String, String>();
}
