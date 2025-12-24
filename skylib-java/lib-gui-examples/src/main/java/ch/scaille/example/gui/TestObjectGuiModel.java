package ch.scaille.example.gui;

import javax.annotation.processing.Generated;

import java.util.Arrays;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.Configuration;

import ch.scaille.javabeans.persisters.IPersisterFactory.ObjectHolder;
import ch.scaille.javabeans.persisters.Persisters;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.javabeans.properties.IntProperty;

@Generated(value = "ch.scaille.gui.mvc.GuiModelGenerator", date = "2025/12/24 17:32", comments = "-sp ch.scaille.example -cp ../lib-gui-examples/target/classes -t ../lib-gui-examples/src/main/java")
public class TestObjectGuiModel extends GuiModel implements IObjectGuiModel<ch.scaille.example.gui.TestObject> {
   
    private final ObjectHolder<ch.scaille.example.gui.TestObject> currentObjectProvider = new ObjectHolder<>();

	public static final String ASECOND_VALUE = "ASecondValue";
	
	public static final String AFIRST_VALUE = "AFirstValue";
	
	

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public TestObjectGuiModel(final String prefix, ModelConfiguration.ModelConfigurationBuilder config) {
		super(config.ifNotSet(modelConfiguration -> GuiModel.createErrorProperty(prefix + "TestObject-Error", modelConfiguration)));
		aSecondValueProperty = new IntProperty(prefix + ASECOND_VALUE, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(TestObject::getASecondValue, TestObject::setASecondValue)),
			implicitConverters(TestObject.class, ASECOND_VALUE, java.lang.Integer.class));
		aFirstValueProperty = new ObjectProperty<java.lang.String>(prefix + AFIRST_VALUE, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.persister(TestObject::getAFirstValue, TestObject::setAFirstValue)),
			implicitConverters(TestObject.class, AFIRST_VALUE, java.lang.String.class));
		
		allProperties = new AbstractProperty[]{aSecondValueProperty, aFirstValueProperty};
    }
            
    public TestObjectGuiModel(ModelConfiguration.ModelConfigurationBuilder config) {
    	this("", config);
    }

	public Class<?> getContainerClass() {
		return ch.scaille.example.gui.TestObject.class;
	}

	public IntProperty getASecondValueProperty() {
	    return aSecondValueProperty;
	}
	public ObjectProperty<java.lang.String> getAFirstValueProperty() {
	    return aFirstValueProperty;
	}
	

    @Override
    public void load() {
    	try {
    		getPropertySupport().transmitChangesOnlyToComponent();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			getPropertySupport().transmitChangesBothWays();
		}
    }

    @Override
    public void save() {
		Arrays.stream(allProperties).forEach(AbstractProperty::save);
    }

    @Override
    public void setCurrentObject(final ch.scaille.example.gui.TestObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ch.scaille.example.gui.TestObject> loadBinding() {
        return new IComponentBinding<>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ch.scaille.example.gui.TestObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final IComponentChangeSource source, final ch.scaille.example.gui.TestObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
