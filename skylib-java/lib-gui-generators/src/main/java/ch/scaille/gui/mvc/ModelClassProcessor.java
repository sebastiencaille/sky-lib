package ch.scaille.gui.mvc;

import static ch.scaille.generators.util.JavaCodeGenerator.toConstant;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;
import ch.scaille.util.dao.metadata.IAttributeMetaData;
import ch.scaille.util.dao.metadata.UntypedDataObjectMetaData;
import ch.scaille.util.helpers.ClassFinder.URLClassFinder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * To generate the code of the MVC model for a given model class
 */
@NullMarked
public class ModelClassProcessor {

    private static final String ATTRIB_PUBLIC = "public ";

    private final GenerationMetadata generationMetadata;

    private final GeneratorContext context;

    private final BeanAccess delegate = new BeanAccess.GetSetAttributeBeanAccess();

    @Nullable
    private final String fixedTargetPackage;

    public ModelClassProcessor(URLClassFinder classFinder,
                               @Nullable String targetPackage,
                               GenerationMetadata generationMetadata) {
        this.fixedTargetPackage = targetPackage;
        this.generationMetadata = generationMetadata;
        this.context = new GeneratorContext(classFinder);
    }

    protected String getClassName(Class<?> modelClass) {
        return modelClass.getSimpleName() + "GuiModel";
    }

    protected boolean includeAttribute(final IAttributeMetaData<?> attrib) {
        return !Modifier.isStatic(attrib.onTypedMetaDataF(AbstractAttributeMetaData::getModifier));
    }

    protected Template process(Class<?> modelClass) {

        context.reset();

        final var generatedModelClassName = getClassName(modelClass);
        final var targetPackage = fixedTargetPackage != null ? fixedTargetPackage
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
        if (fixedTargetPackage != null) {
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
     */
    protected void addAttributesDeclarations(final UntypedDataObjectMetaData metaData) {

        forEachAttribute(metaData, attrib -> context.append("fields.declareStatic", generateAccessConstants(attrib)));
        context.append("fields.initStatic", "");

        forEachAttribute(metaData, attrib -> context.append("fields.declare",
                AttributeProcessor.create(context, attrib, delegate).addImports().generateDeclaration() + "\n"));

        forEachAttribute(metaData, attrib -> {
            final var attribProcessor = AttributeProcessor.create(context, attrib, delegate);
            context.append("fields.init",
                    attribProcessor.addImports().generateInitialization(metaData.getDataType()) + "\n");
        });
    }

    protected void forEachAttribute(final UntypedDataObjectMetaData metaData,
                                    final Consumer<IAttributeMetaData<?>> attributeApplier) {
        metaData.getAttributes().stream().filter(this::includeAttribute).forEach(attributeApplier);
    }

    protected String generateLoadFrom(final IAttributeMetaData<?> attrib) {
        return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName() + ".load(this);";
    }

    protected String generatePropertyNameOf(final IAttributeMetaData<?> attrib) {
        return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName();
    }

    protected String generateSaveInto(final IAttributeMetaData<?> attrib) {
        return AttributeProcessor.create(context, attrib, delegate).getPropertyFieldName() + ".save();";
    }

    protected String generateAccessConstants(final IAttributeMetaData<?> attrib) {
        final var gen = JavaCodeGenerator.inMemory();
        gen.appendIndentedLine(
                "public static final String " + toConstant(attrib.getName()) + " = \"" + attrib.getName() + "\";");
        gen.eol();
        return gen.toString();
    }

    protected String generateFieldConstants(final IAttributeMetaData<?> attrib) {
        final var gen = JavaCodeGenerator.inMemory();
        final var constant = toConstant(attrib.getName());
        final var fieldConstant = constant + "_FIELD";
        gen.appendIndentedLine("private static final Field " + fieldConstant + ';');
        gen.eol();

        return gen.toString();
    }

    protected String generateGetter(final IAttributeMetaData<?> attrib) {
        final var processor = AttributeProcessor.create(context, attrib, delegate);
        final var gen = JavaCodeGenerator.inMemory();
        gen.openBlock(ATTRIB_PUBLIC, processor.getPropertyType(), " get", attrib.getName(), "Property()");
        gen.appendIndentedLine("return " + processor.getPropertyFieldName() + ";");
        gen.closeBlock();
        return gen.toString();
    }
}