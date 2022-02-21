package ch.scaille.tcwriter.recording;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import ch.scaille.tcwriter.tc.TestObjectDescription;

public class RecorderTestActors {

	private static Map<Object, TestObjectDescription> descriptions = new HashMap<>();
	private static Map<Object, String> names = new HashMap<>();

	private RecorderTestActors() {
	}

	/**
	 *
	 * @param testActor   the actor
	 * @param modelName   the name of the actor in the model (optional)
	 * @param description a description
	 */
	public static void register(final Object testActor, final String modelName,
			final TestObjectDescription description) {
		if (modelName == null && description == null) {
			throw new InvalidParameterException("Either modelName or description must be provided");
		}
		if (modelName != null) {
			names.put(testActor, modelName);
		}
		if (description == null) {
			descriptions.put(testActor, description);
		}
	}

	public static Map<Object, String> getNames() {
		return names;
	}

	public static Map<Object, TestObjectDescription> getDescriptions() {
		return descriptions;
	}

}
