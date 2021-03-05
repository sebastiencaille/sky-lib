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
	final AbstractAttributeMetaData<?> attrib;
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
		this.attrib = attrib;
		this.delegate = delegate;
	}

	protected String getTypeAsString() {
		return ModelClassProcessor.typeToString(attrib.getGenericType());
	}

	protected String getObjectTypeAsString() {
		return ModelClassProcessor.typeToString(attrib.getGenericType());
	}

	String getPropertyName() {
		return toFirstLetterInLowerCase(attrib.getName()) + "Property";
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
			return toFirstLetterInUpperCase(attrib.getType().getName() + "Property");
		}

		@Override
		protected String getFieldCreation() {
			return delegate.getPrimitiveFieldCreation(this);
		}

		@Override
		protected String getObjectTypeAsString() {
			switch (attrib.getType().getSimpleName()) {
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
				return "java.lang." + toFirstLetterInUpperCase(attrib.getType().getSimpleName());
			}
		}

		@Override
		public String getter() {
			if (Boolean.class.equals(attrib.getType()) || Boolean.TYPE.equals(attrib.getType())) {
				return "is" + attrib.getName();
			}
			return "get" + attrib.getName();
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
			return objectName + ModelClassProcessor.typeParametersToString(attrib.getGenericType());
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
				"%s = new %s(prefix + \"-%s\",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, %s), Configuration.errorNotifier(errorProperty));",
				getPropertyName(), getPropertyType(), attrib.getName(), getFieldCreation());
	}

	String generateInitialization() {
		return String.format(
				"%s = new %s(prefix + \"-%s\",  propertySupport).configureTyped(Configuration.persistent(currentObjectProvider, %s), Configuration.errorNotifier(errorProperty));",
				getPropertyName(), getPropertyType(), attrib.getName(), getFieldCreation());
	}

	public String getConstantName() {
		return toConstant(attrib.getName());
	}

	public static class GetSetAttributeDelegate implements AttributeProcessorDelegate {
		@Override
		public String getFieldCreation(final AttributeProcessor attributeProcessor) {
			final AbstractAttributeMetaData<?> attr = attributeProcessor.attrib;

			String setter;
			if (!attributeProcessor.attrib.isReadOnly()) {
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
		return "get" + attrib.getName();
	}

	public String setter() {
		return "set" + attrib.getName();
	}

}
