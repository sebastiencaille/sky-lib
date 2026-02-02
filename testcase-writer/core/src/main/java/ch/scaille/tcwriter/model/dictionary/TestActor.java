package ch.scaille.tcwriter.model.dictionary;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.NamedObject;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Getter
@Setter
public class TestActor extends NamedObject {

    public static final TestActor NOT_SET = new TestActor(IdObject.ID_NOT_SET, "", TestRole.NOT_SET);
    private TestRole role = TestRole.NOT_SET;

    protected TestActor() {
        super(null, null);
        this.role = TestRole.NOT_SET;
    }

    public TestActor(final String id, final String name, final TestRole role) {
        super(id, name);
        this.role = role;
    }

    public void setRole(TestRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
