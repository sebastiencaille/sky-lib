package ch.skymarshall.tcwriter.generators.visitors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class JsonHelper {

	private static final String CONTEXT_ALL_REFERENCES = "AllTestReferences";
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper().configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_FIELDS, true).setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
				.enableDefaultTyping(DefaultTyping.NON_FINAL, As.WRAPPER_OBJECT);

		final SimpleModule testCaseWriterModule = new SimpleModule("TestCaseWriter");
		testCaseWriterModule.addDeserializer(ExportReference.class, new JsonDeserializer<ExportReference>() {
			@Override
			public ExportReference deserialize(final JsonParser p, final DeserializationContext ctxt)
					throws IOException {
				if (!ExportReference.class.getName().equals(p.getCurrentName())) {
					throw new IllegalStateException("Unexpected type in ExportReference: " + p.getCurrentName());
				}
				final TreeNode content = p.readValueAsTree();
				final TreeNode id = content.get("id");
				if (id == null) {
					throw new IllegalStateException("Unexpected attribute in ExportReference: " + p.getCurrentName());
				}
				final ExportReference exportReference = new ExportReference(((TextNode) id).asText());
				((List<ExportReference>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES)).add(exportReference);
				return exportReference;
			}
		});

		mapper.registerModules(new GuavaModule(), testCaseWriterModule);

	}

	private JsonHelper() {
	}

	public static String toJson(final TestCase tc) throws IOException {
		return mapper.writerFor(TestCase.class).writeValueAsString(tc);
	}

	public static String toJson(final TestModel tm) throws IOException {
		return mapper.writerFor(TestModel.class).writeValueAsString(tm);
	}

	public static TestCase testCaseFromJson(final String str, final TestModel testModel) throws IOException {
		final ArrayList<ExportReference> references = new ArrayList<>();
		final ContextAttributes ctxt = mapper.getDeserializationConfig().getAttributes()
				.withPerCallAttribute(CONTEXT_ALL_REFERENCES, references);
		final TestCase testCase = mapper.readerFor(TestCase.class).with(ctxt).readValue(str);
		testCase.setModel(testModel);
		references.forEach(e -> e.restore(testCase));
		return testCase;
	}

	public static TestModel testModelFromJson(final String str) throws IOException {
		return mapper.readerFor(TestModel.class).readValue(str);
	}

}
