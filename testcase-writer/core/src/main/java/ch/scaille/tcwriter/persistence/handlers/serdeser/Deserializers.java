package ch.scaille.tcwriter.persistence.handlers.serdeser;

import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestActorMixin;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestParameterValueMixin;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestReferenceMixin;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestStepMixin;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface Deserializers {

    List<ReferenceHandler<TestDictionary, ?>> DICTIONARY_HANDLERS = List.of(TestActorMixin.REF_HANDLER);

    static Optional<ReferenceHandler<TestDictionary, ?>> getDictionaryHandler(Object beanOrClass, String propertyName) {
        return DICTIONARY_HANDLERS.stream()
                .filter(v -> v.matches(beanOrClass, propertyName))
                .findFirst();
    }

    List<ReferenceHandler<TestCase, ?>> TESTCASE_HANDLERS = List.of(TestStepMixin.ROLE_REF_HANDLER, TestStepMixin.ACTOR_REF_HANDLER, TestStepMixin.ACTION_REF_HANDLER,
            TestReferenceMixin.REF_HANDLER, TestParameterValueMixin.REF_HANDLER);

    static Optional<ReferenceHandler<TestCase, ?>> getTestCaseHandler(Object beanOrClass, String propertyName) {
        return TESTCASE_HANDLERS.stream()
                .filter(v -> v.matches(beanOrClass, propertyName))
                .findFirst();
    }

    String CONTEXT_ALL_REFERENCES = "AllTestReferences";
    String CONTEXT_DICTIONARY = "DictionaryLoaderFunction";

    SimpleModule TCVWRITER_MODULE = new SimpleModule("TCWriter")
            .setMixInAnnotation(TestActor.class, TestActorMixin.class)
            .setMixInAnnotation(TestReference.class, TestReferenceMixin.class)
            .setMixInAnnotation(TestStep.class, TestStepMixin.class)
            .setMixInAnnotation(TestParameterValue.class, TestParameterValueMixin.class)
            .setDeserializerModifier(new ValueDeserializerModifier() {
                @Override
                public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
                    if (TestDictionary.class.equals(beanDescRef.getBeanClass())) {
                        return new TestDictionaryValueDeserializer((ValueDeserializer<TestDictionary>) deserializer);
                    } else if (TestCase.class.equals(beanDescRef.getBeanClass())) {
                        return new TestCaseValueDeserializer((ValueDeserializer<TestCase>) deserializer);
                    }
                    return super.modifyDeserializer(config, beanDescRef, deserializer);
                }
            });


    class TestDictionaryValueDeserializer extends DelegatingDeserializer {
        public TestDictionaryValueDeserializer(ValueDeserializer<TestDictionary> deserializer) {
            super(deserializer);
        }


        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegatee) {
            return this;
        }

        @Override
        public TestDictionary deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            final var dictionary = (TestDictionary) super.deserialize(p, ctxt);
            final var references = (List<ExportReference<TestDictionary, ?>>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES);
            if (references != null) {
                references.forEach(e -> e.apply(dictionary));
            }
            return dictionary;
        }
    }

    class TestCaseValueDeserializer extends DelegatingDeserializer {

        public TestCaseValueDeserializer(ValueDeserializer<TestCase> deserializer) {
            super(deserializer);
        }


        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegatee) {
            return this;
        }

        @Override
        public TestCase deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            final var testCase = (TestCase) super.deserialize(p, ctxt);
            testCase.setDictionary((TestDictionary) ctxt.getAttribute(CONTEXT_DICTIONARY));
            final var references = (List<ExportReference<TestCase, ?>>) ctxt.getAttribute(CONTEXT_ALL_REFERENCES);
            if (references != null) {
                references.forEach(e -> e.apply(testCase));
            }
            testCase.republishReferences();
            return testCase;
        }
    }
}
