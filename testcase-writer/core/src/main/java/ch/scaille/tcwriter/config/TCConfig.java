package ch.scaille.tcwriter.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TCConfig {
	
	private String name;

	private List<Object> subconfigs = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Object> getSubconfigs() {
		return subconfigs;
	}

	public void setSubconfigs(List<Object> subconfigs) {
		this.subconfigs = subconfigs;
	}

	public <T> Optional<T> getSubconfig(Class<T> clazz) {
		return subconfigs.stream().filter(clazz::isInstance).findFirst().map(clazz::cast);
	}

	public static TCConfig of(String name, Object subConfig) {
		TCConfig config = new TCConfig();
		config.name = name;
		config.subconfigs.add(subConfig);
		return config;
	}
}
