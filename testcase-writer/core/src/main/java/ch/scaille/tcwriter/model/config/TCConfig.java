package ch.scaille.tcwriter.model.config;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record TCConfig(String name, List<SubConfig> subconfigs) {

	public static final String DEFAULT = "default";

	public String getName() {
		return name;
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type")
	public List<SubConfig> getSubconfigs() {
		return subconfigs;
	}

	public <T extends SubConfig> Optional<T> getSubconfig(Class<T> clazz) {
		return subconfigs.stream().filter(clazz::isInstance).findFirst().map(clazz::cast);
	}

	public static TCConfig of(String name, SubConfig... subConfig) {
		return new TCConfig(name, List.of(subConfig));
	}
}
