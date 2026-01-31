package ch.scaille.tcwriter.model.dictionary;

import java.util.Map;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.NamedObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class TestActor extends NamedObject {

    public static final TestActor NOT_SET = new TestActor(IdObject.ID_NOT_SET, "", TestRole.NOT_SET);
    private final String roleId;
    @JsonIgnore
    private TestRole role;

    protected TestActor() {
        super(null, null);
        this.role = null;
        this.roleId = null;
    }

    public TestActor(final String id, final String name, final TestRole role) {
        super(id, name);
        this.role = role;
        this.roleId = role.getId();
    }

    @JsonCreator
    public TestActor(final String id, final String name, final String roleId) {
        super(id, name);
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void restore(Map<String, TestRole> roles) {
        this.role = roles.get(this.roleId);
    }
}
