package ch.skymarshall.gui.mvc;

import static ch.skymarshall.util.dao.metadata.MetadataHelper.toFirstLetterInLowerCase;
import static ch.skymarshall.util.dao.metadata.MetadataHelper.toFirstLetterInUpperCase;
import static ch.skymarshall.util.generators.JavaCodeGenerator.toConstant;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.gui.mvc.ModelClassProcessor.Context;
import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;

public abstract class AttributeProcessor {

	final AbstractAttributeMetaData<?> modelAttribute;
	final Context context;
	protected final AttributeProcessorDelegate delegate;

	protected abstract String getPropertyType();

	AttributeProcessor addImports() {
		delegate.addImports(this);
		return this;
	}

	interface AttributeProcessorDelegate {

		String getFieldCreation(AttributeProcessor attributeProcessor);

		void addImports(AttributeProcessor attributeProcessor);

		String getPrimitiveFieldCreation(PrimitiveProcessor primitiveProcessor);

	}

	protected AttributeProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
			final AttributeProcessorDelegate delegate) {
		this.context = context;
		this.modelAttribute = attrib;
		this.delegate = delegate;
	}

	protected String getTypeAsString() {
		return ModelClassProcessor.typeToString(modelAttribute.getGenericType());
	}

	protected String getModelTypeAsString() {
		return ModelClassProcessor.typeToString(modelAttribute.getGenericType());
	}

	String getPropertyName() {
		return toFirstLetterInLowerCase(modelAttribute.getName()) + "Property";
	}

	protected String getFieldCreation() {
		return delegate.getFieldCreation(this);
	}

	static AttributeProcessor create(final Context context, final AbstractAttributeMetaData<?> attrib,
			final AttributeProcessorDelegate delegate) {
		final Class<?> type = attrib.getType();
		if (type.isPrimitive()) {
			return new PrimitiveProcessor(context, attrib, delegate);
		} else if (Set.class.isAssignableFrom(type)) {
			return new SetProcessor(context, attrib, delegate);
		} else if (Map.class.isAssignableFrom(type)) {
			return new MapProcessor(context, attrib, delegate);
		} else if (List.class.isAssignableFrom(type)) {
			return new ListProcessor(context, attrib, delegate);
		}
		return new ObjectProcessor(context, attrib, delegate);

	}

	static class PrimitiveProcessor extends AttributeProcessor {

		public PrimitiveProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, delegate);
		}

		@Override
		protected String getPropertyType() {
			return toFirstLetterInUpperCase(modelAttribute.getType().getName() + "Property");
		}

		@Override
		protected String getFieldCreation() {
			return delegate.getPrimitiveFieldCreation(this);
		}

		@Override
		protected String getModelTypeAsString() {
			switch (modelAttribute.getType().getSimpleName()) {
			case "short":
				return Short.class.getName();
			case "int":
				return Integer.class.getName();
			case "long":
				return Long.class.getName();
			case "float":
				return Float.class.getName();
			case "double":
				return Double.class.getName();
			case "char":
				return Character.class.getName();
			case "boolean":
				return Boolean.class.getName();
			default:
				return "java.lang." + toFirstLetterInUpperCase(modelAttribute.getType().getSimpleName());
			}
		}

		@Override
		public String getter() {
			if (Boolean.class.equals(modelAttribute.getType()) || Boolean.TYPE.equals(modelAttribute.getType())) {
				return "is" + modelAttribute.getName();
			}
			return "get" + modelAttribute.getName();
		}

		@Override
		AttributeProcessor addImports() {
			super.addImports();
			context.addImport(getPropertyType());
			return this;
		}

	}

	static class ContainerProcessorWithType extends AttributeProcessor {

		private final String objectName;

		public ContainerProcessorWithType(final Context context, final AbstractAttributeMetaData<?> attrib,
				final String objectName, final AttributeProcessorDelegate delegate) {
			super(context, attrib, delegate);
			this.objectName = objectName;
		}

		@Override
		protected String getPropertyType() {
			return objectName + ModelClassProcessor.typeParametersToString(modelAttribute.getGenericType());
		}

		@Override
		protected AttributeProcessor addImports() {
			super.addImports();
			context.addImport(objectName);
			return this;
		}

		@Override
		String generateInitialization() {
			return generateInitializationWithType();
		}
	}

	static class SetProcessor extends ContainerProcessorWithType {

		public SetProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "SetProperty", delegate);
		}

	}

	static class MapProcessor extends ContainerProcessorWithType {

		public MapProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "MapProperty", delegate);
		}
	}

	static class ListProcessor extends ContainerProcessorWithType {

		public ListProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "ListProperty", delegate);
		}
	}

	static class ObjectProcessor extends AttributeProcessor {

		public ObjectProcessor(final Context context, final AbstractAttributeMetaData<?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, delegate);
		}

		@Override
		AttributeProcessor addImports() {
			super.addImports();
			context.addImport("ObjectProperty");
			return this;
		}

		@Override
		protected String getPropertyType() {
			return "ObjectProperty<" + getTypeAsString() + ">";
		}

	}

	String generateDeclaration() {
		return String.format("protected final %s %s;", getPropertyType(), getPropertyName());
	}

	String generateInitializationWithType() {
		return String.format(
				"%s = new %s(prefix + \"%s\", this).configureTyped(Configuration.persistent(currentObjectProvider, %s));",
				getPropertyName(), getPropertyType(), modelAttribute.getName(), getFieldCreation());
	}

	String generateInitialization() {
		return String.format(
				"%s = new %s(prefix + \"%s\", this).configureTyped(Configuration.persistent(currentObjectProvider, %s));",
				getPropertyName(), getPropertyType(), modelAttribute.getName(), getFieldCreation());
	}

	String generateImplicitConverters(Class<?> modelClass) {
		return String.format(
				"config.getImplicitConverters().stream().sequential().map(c -> ((ImplicitConvertProvider<%s, %s>)c).create(%s.class, \"%s\", %s.class)).forEach(%s::addImplicitConverter);",
				modelClass.getSimpleName(), getModelTypeAsString(), modelClass.getSimpleName(),
				modelAttribute.getName(), getModelTypeAsString(), getPropertyName());
	}

	public String getConstantName() {
		return toConstant(modelAttribute.getName());
	}

	public static class GetSetAttributeDelegate implements AttributeProcessorDelegate {
		@Override
		public String getFieldCreation(final AttributeProcessor attributeProcessor) {
			final AbstractAttributeMetaData<?> attr = attributeProcessor.modelAttribute;

			String setter;
			if (!attributeProcessor.modelAttribute.isReadOnly()) {
				setter = attr.getDeclaringType().getSimpleName() + "::" + attributeProcessor.setter();
			} else {
				setter = "null";
			}

			return "Persisters.getSet(" + attr.getDeclaringType().getSimpleName() + "::" + attributeProcessor.getter()
					+ ", " + setter + ")";
		}

		@Override
		public String getPrimitiveFieldCreation(final PrimitiveProcessor attributeProcessor) {
			return getFieldCreation(attributeProcessor);
		}

		@Override
		public void addImports(AttributeProcessor attributeProcessor) {
			// noop
		}

	}

	public String getter() {
		return "get" + modelAttribute.getName();
	}

	public String setter() {
		return "set" + modelAttribute.getName();
	}

}
