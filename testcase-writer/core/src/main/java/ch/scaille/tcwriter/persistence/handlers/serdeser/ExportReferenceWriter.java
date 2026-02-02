package ch.scaille.tcwriter.persistence.handlers.serdeser;

import lombok.NoArgsConstructor;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.ser.VirtualBeanPropertyWriter;
import tools.jackson.databind.util.Annotations;

import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor
public class ExportReferenceWriter<T> extends VirtualBeanPropertyWriter {

    private ReferenceHandler<?, T> handler;

    public ExportReferenceWriter(ReferenceHandler<?, T> handler,
                                 BeanPropertyDefinition propDef,
                                 Annotations contextAnnotations,
                                 JavaType declaredType) {
        super(propDef, contextAnnotations, declaredType);
        this.handler = handler;
    }

    @Override
    protected Object value(Object bean, JsonGenerator g, SerializationContext context) throws Exception {
        return new ExportReference<>(null, handler.exporter().apply((T) bean), null);
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config,
                                                AnnotatedClass declaringClass,
                                                BeanPropertyDefinition propDef,
                                                JavaType type) {
        final Optional<ReferenceHandler<?, ?>> propertyHandler = Deserializers.getTestCaseHandler(declaringClass.getType().getRawClass(), propDef.getName()).map(Function.identity());
        final var safeHandler = propertyHandler.or(() -> Deserializers.getDictionaryHandler(declaringClass.getType().getRawClass(), propDef.getName()))
                .orElseThrow(() -> new IllegalStateException("Unable to find handler for " + declaringClass.getType().getRawClass().getName() + "." + propDef.getName()));
        return new ExportReferenceWriter<>(safeHandler, propDef, declaringClass.getAnnotations(), type);
    }


}