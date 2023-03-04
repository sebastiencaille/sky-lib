package ch.scaille.tcwriter.recorder;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class RecorderTestActors {

	private static final Map<Object, String> descriptions = new HashMap<>();
	private static final Map<Object, String> names = new HashMap<>();

	private RecorderTestActors() {
	}

	/**
	 *
	 * @param testActor   the actor, which must be the instance used during the test
	 * @param modelName   the name of the actor
	 * @param description a description
	 */
	public static <T> T register(final T testActor, final String modelName, final String description) {
		if (modelName == null && description == null) {
			throw new InvalidParameterException("Either modelName or description must be provided");
		}
		if (modelName != null) {
			names.put(testActor, modelName);
		}
		if (description != null) {
			descriptions.put(testActor, description);
		}
		return testActor;
	}

	public static Map<Object, String> getNames() {
		return names;
	}

	public static Map<Object, String> getDescriptions() {
		return descriptions;
	}

}
