package ch.skymarshall.dataflowmgr.generator.readers.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import ch.skymarshall.dataflowmgr.generator.model.Action;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;

public class ActionJson {

	public static class ActionDeserializer extends StdDeserializer<ActionPoint> {

		private static final ObjectMapper mapper = new ObjectMapper();

		public ActionDeserializer() {
			super(ActionPoint.class);
		}

		@Override
		public ActionPoint deserialize(final JsonParser jp, final DeserializationContext ctxt)
				throws IOException, JsonProcessingException {

			final TreeNode node = jp.getCodec().readTree(jp);

			final Map<String, JsonNode> filtered = new HashMap<>();
			Action action = null;
			final Iterator<String> fieldNames = node.fieldNames();
			while (fieldNames.hasNext()) {
				final String fieldName = fieldNames.next();
				final TreeNode treeNode = node.get(fieldName);
				if (fieldName.startsWith("action-")) {
					if (action != null) {
						throw new JsonParseException(jp, "Second action specified");
					}
					action = new Action();
					action.template = fieldName.substring("action-".length());
					action.content = ((ValueNode) treeNode).asText();
				} else if ("action".equals(fieldName)) {
					action = new Action();
					action.template = "";
					action.content = ((ValueNode) treeNode).asText();
				} else {
					filtered.put(fieldName, (JsonNode) treeNode);
				}
			}
			if (action == null) {
				throw new JsonParseException(jp, "No action specified");
			}

			final ActionPoint actionPoint = mapper.treeToValue(new ObjectNode(ctxt.getNodeFactory(), filtered),
					ActionPoint.class);
			actionPoint.action = action;
			return actionPoint;
		}

	}

}
