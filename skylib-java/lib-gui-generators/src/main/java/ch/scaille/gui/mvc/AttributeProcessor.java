package ch.scaille.gui.mvc;

import static ch.scaille.generators.util.JavaCodeGenerator.toConstant;
import static ch.scaille.util.dao.metadata.MetadataHelper.toFirstLetterInLowerCase;
import static ch.scaille.util.dao.metadata.MetadataHelper.toFirstLetterInUpperCase;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;
import ch.scaille.util.dao.metadata.IAttributeMetaData;

/**
 * To generate the code of the MVC model for a given model class attribute
 */
public abstract class AttributeProcessor {

	public static AttributeProcessor create(final GeneratorContext context, final IAttributeMetaData<?> a,
			final AttributeProcessorDelegate delegate) {
		return a.onTypedMetaDataF(attrib -> {
			final var type = attrib.getType();
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
		});
	}

	public static class PrimitiveProcessor extends AttributeProcessor {

		public PrimitiveProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
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

	public static class ContainerProcessorWithType extends AttributeProcessor {

		private final String objectName;

		public ContainerProcessorWithType(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
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

	}

	public static class SetProcessor extends ContainerProcessorWithType {

		public SetProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "SetProperty", delegate);
		}

	}

	public static class MapProcessor extends ContainerProcessorWithType {

		public MapProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "MapProperty", delegate);
		}
	}

	public static class ListProcessor extends ContainerProcessorWithType {

		public ListProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
				final AttributeProcessorDelegate delegate) {
			super(context, attrib, "ListProperty", delegate);
		}
	}

	public static class ObjectProcessor extends AttributeProcessor {

		public ObjectProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
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

	public static class GetSetAttributeDelegate implements AttributeProcessorDelegate {
		@Override
		public String getFieldCreation(final AttributeProcessor attributeProcessor) {
			final var attr = attributeProcessor.modelAttribute;

			String setter;
			if (!attributeProcessor.modelAttribute.isReadOnly()) {
				setter = attr.getDeclaringType().getSimpleName() + "::" + attributeProcessor.setter();
			} else {
				setter = "null";
			}
			return String.format("Persisters.persister(%s::%s, %s)", attr.getDeclaringType().getSimpleName(),
					attributeProcessor.getter(), setter);
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

	final AbstractAttributeMetaData<?, ?> modelAttribute;
	final GeneratorContext context;
	protected final AttributeProcessorDelegate delegate;

	protected abstract String getPropertyType();

	AttributeProcessor addImports() {
		delegate.addImports(this);
		return this;
	}

	protected AttributeProcessor(final GeneratorContext context, final AbstractAttributeMetaData<?, ?> attrib,
			final AttributeProcessorDelegate delegate) {
		this.context = context;
		this.modelAttribute = attrib;
		this.delegate = delegate;
	}

	protected String getTypeAsString() {
		return modelAttribute.getGenericType().getTypeName();
	}

	String getPropertyFieldName() {
		return toFirstLetterInLowerCase(modelAttribute.getName()) + "Property";
	}

	protected String getFieldCreation() {
		return delegate.getFieldCreation(this);
	}

	String generateDeclaration() {
		return String.format("protected final %s %s;", getPropertyType(), getPropertyFieldName());
	}

	String generateInitialization(Class<?> modelClass) {
		return String.format(
				"%s = new %s(prefix + %s, this).configureTyped(%n"
						+ "\tConfiguration.persistent(currentObjectProvider, %s),%n"
						+ "\timplicitConverters(%s.class, %s, %s.class));",
				getPropertyFieldName(), getPropertyType(), getAttributeNameConstant(), getFieldCreation(),
				modelClass.getSimpleName(), getAttributeNameConstant(),
				modelAttribute.getClassType().getCanonicalName());
	}

	public String getAttributeNameConstant() {
		return toConstant(modelAttribute.getName());
	}

	public String getter() {
		return "get" + modelAttribute.getName();
	}

	public String setter() {
		return "set" + modelAttribute.getName();
	}

}
