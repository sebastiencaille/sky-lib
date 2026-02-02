package ch.scaille.tcwriter.persistence.handlers.serdeser.mixins;

import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReference;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReferenceWriter;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ReferenceHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.annotation.JsonAppend;

import static ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestActorMixin.ROLE_REF;

@JsonIgnoreProperties("role")
@JsonAppend(props = {
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = ROLE_REF, type = ExportReference.class)
})
public class TestActorMixin {

    private TestActorMixin() {
        /* This utility class should not be instantiated */
    }

    public static final String ROLE_REF = "roleRef";

    public static final ReferenceHandler<TestDictionary, TestActor> REF_HANDLER =
            new ReferenceHandler<>(TestActor.class, ROLE_REF,
                    actor -> actor.getRole().getId(),
                    (dictionary, actor, reference) -> actor.setRole(dictionary.getRoles().get(reference)));
}
