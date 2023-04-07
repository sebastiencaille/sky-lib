package ch.scaille.tcwriter.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class TCConfig {

	public static final String DEFAULT = "default";

	private String name = DEFAULT;

	private List<Object> subconfigs = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type")
	public List<Object> getSubconfigs() {
		return subconfigs;
	}

	public void setSubconfigs(List<Object> subconfigs) {
		this.subconfigs = subconfigs;
	}

	public <T> Optional<T> getSubconfig(Class<T> clazz) {
		return subconfigs.stream().filter(clazz::isInstance).findFirst().map(clazz::cast);
	}

	public static TCConfig of(String name, Object... subConfig) {
		final var config = new TCConfig();
		config.name = name;
		config.subconfigs.addAll(Arrays.asList(subConfig));
		return config;
	}
}
