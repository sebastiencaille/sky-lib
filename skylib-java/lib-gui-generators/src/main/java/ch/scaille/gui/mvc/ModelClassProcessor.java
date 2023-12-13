package ch.scaille.gui.mvc;

import static ch.scaille.generators.util.JavaCodeGenerator.toConstant;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;
import ch.scaille.gui.mvc.AttributeProcessor.AttributeProcessorDelegate;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;
import ch.scaille.util.dao.metadata.UntypedDataObjectMetaData;
import ch.scaille.util.helpers.ClassFinder.URLClassFinder;

/**
 * To generate the code of the MVC model for a given model class
 */
public class ModelClassProcessor {

	private static final String ATTRIB_PUBLIC = "public ";

	public static class GeneratorContext {
		private final URLClassFinder classFinder;
		private final Map<String, String> properties = new HashMap<>();
		private final Set<String> imports = new HashSet<>();
		private final Map<String, String> generatedConstants = new HashMap<>();

		public GeneratorContext(URLClassFinder classFinder) {
			this.classFinder = classFinder;
		}

		public void addImport(final Class<?> class1) {
			imports.add(class1.getName());
		}

		public void append(final String key, final String value) {
			Template.append(properties, key, value);
		}

		public void appendToList(final String key, final String value) {
			Template.appendToList(properties, key, value);
		}

		public void addImport(final String className) {
			imports.add(classFinder.loadByName(className).getName());
		}
		
		public void reset() {
			imports.clear();
			properties.clear();
			generatedConstants.clear();
		}
		
	}

	public static String typeParametersToString(final Type type) {
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Unhandled type " + type);
		}
		final var parameterizedType = (ParameterizedType) type;
		final var textOutput = new StringBuilder();
		var sep = "<";
		for (final var argType : parameterizedType.getActualTypeArguments()) {
			textOutput.append(sep).append(argType.getTypeName());
			sep = ", ";
		}
		textOutput.append('>');
		return textOutput.toString();
	}

	private final GenerationMetadata generationMetadata;

	private final GeneratorContext context;

	private final AttributeProcessorDelegate delegate = new AttributeProcessor.GetSetAttributeDelegate();

	private final String defaultTargetPackage;

	public ModelClassProcessor(URLClassFinder classFinder, String targetPackage,
			GenerationMetadata generationMetadata) {
		this.defaultTargetPackage = targetPackage;
		this.generationMetadata = generationMetadata;
		this.context = new GeneratorContext(classFinder);
	}

	protected String getClassName(Class<?> modelClass) {
		return modelClass.getSimpleName() + "GuiModel";
	}

	protected boolean includeAttribute(final AbstractAttributeMetaData<?> attrib) {
		return !Modifier.isStatic(attrib.getModifier());
	}

	protected Template process(Class<?> modelClass) {

		context.reset();
		
		final var generatedModelClassName = getClassName(modelClass);
		final var targetPackage = defaultTargetPackage != null ? defaultTargetPackage
				: modelClass.getPackage().getName();

		final var metaData = new UntypedDataObjectMetaData(modelClass, false);

		context.properties.put("modelClass", generatedModelClassName);
		context.properties.put("objectClass", modelClass.getName());
		context.properties.put("objectClassSimpleName", modelClass.getSimpleName());

		// attributes
		addAttributesDeclarations(metaData);
		addAttributesGetters(metaData);
		addAttributePersistenceMethods(metaData);
		
		// imports
		if (defaultTargetPackage != null) {
			context.addImport(modelClass);
		}
		context.properties.put("imports", JavaCodeGenerator.toImports(context.imports));
		context.properties.put("package", targetPackage);
		try {
			return Template.from("templates/guiModel.template")
					.apply(context.properties,
							JavaCodeGenerator.toSourceFilename(targetPackage, generatedModelClassName),
							generationMetadata);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load template", e);
		}
	}

	protected void addAttributesGetters(final UntypedDataObjectMetaData metaData) {
		forEachAttribute(metaData, attrib -> context.append("fields.getters", generateGetter(attrib)));
	}

	protected void addAttributePersistenceMethods(final UntypedDataObjectMetaData metaData) {
		forEachAttribute(metaData, attrib -> context.appendToList("properties.all", generatePropertyNameOf(attrib)));
	}

	/**
	 * Creates attributes related constants and declarations
	 *
	 * @param gen
	 * @param metaData
	 * @throws IOException
	 */
	protected void addAttributesDeclarations(final UntypedDataObjectMetaData metaData) {

		forEachAttribute(metaData, attrib -> context.append("fields.declareStatic", generateAccessConstants(attrib)));
		context.append("fields.initStatic", "");

		forEachAttribute(metaData, attrib -> context.append("fields.declare",
				AttributeProcessor.create(context, attrib, delegate).addImports().generateDeclaration() + "\n"));

		forEachAttribute(metaData, attrib -> {
			AttributeProcessor attribProcessor = AttributeProcessor.create(context, attrib, delegate);
			context.append("fields.init",
					attribProcessor.addImports().generateInitialization(metaData.getDataType()) + "\n");
		});
	}

	protected void forEachAttribute(final UntypedDataObjectMetaData metaData,
			final Consumer<AbstractAttributeMetaData<?>> attributeApplier) {
		metaData.getAttributes().stream().filter(this::includeAttribute).forEach(attributeApplier::accept);
	}

	protected String generateLoadFrom(final AbstractAttributeMetaData<?> attrib) {
		return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName() + ".load(this);";
	}

	protected String generatePropertyNameOf(final AbstractAttributeMetaData<?> attrib) {
		return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName();
	}

	protected String generateSaveInto(final AbstractAttributeMetaData<?> attrib) {
		return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName() + ".save();";
	}

	protected String generateAccessConstants(final AbstractAttributeMetaData<?> attrib) {
		final var gen = JavaCodeGenerator.inMemory();
		gen.appendIndentedLine(
				"public static final String " + toConstant(attrib.getName()) + " = \"" + attrib.getName() + "\";");
		gen.eol();
		return gen.toString();
	}

	protected String generateFieldConstants(final AbstractAttributeMetaData<?> attrib) {
		final var gen = JavaCodeGenerator.inMemory();
		final var constant = toConstant(attrib.getName());
		final var fieldConstant = constant + "_FIELD";
		context.generatedConstants.put(fieldConstant, attrib.getCodeName());
		gen.appendIndentedLine("private static final Field " + fieldConstant + ';');
		gen.eol();

		return gen.toString();
	}

	protected String generateGetter(final AbstractAttributeMetaData<?> attrib) {
		final var processor = AttributeProcessor.create(context, attrib, delegate);
		final var gen = JavaCodeGenerator.inMemory();
		gen.openBlock(ATTRIB_PUBLIC, processor.getPropertyType(), " get", attrib.getName(), "Property()");
		gen.appendIndentedLine("return " + processor.getPropertyFieldName() + ";");
		gen.closeBlock();
		return gen.toString();
	}
}