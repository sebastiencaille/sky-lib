package ch.scaille.example.gui;

import javax.annotation.processing.Generated;

import java.util.Arrays;

import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IObjectGuiModel;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.persisters.Persisters;
import ch.scaille.javabeans.persisters.ObjectProviderPersister;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.Configuration;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.javabeans.properties.IntProperty;

@Generated(value = "ch.scaille.gui.mvc.GuiModelGenerator", date = "2023/12/02 12:06", comments = "-sp ch.scaille.example.gui -s /home/scaille/src/github/sky-lib/skylib-java/lib-gui-examples/target/classes -t /home/scaille/src/github/sky-lib/skylib-java/lib-gui-examples/src/main/java")
public class TestObjectGuiModel extends GuiModel implements IObjectGuiModel<TestObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider<TestObject> currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider<>();

	public static final String ASECOND_VALUE = "ASecondValue";
	
	public static final String AFIRST_VALUE = "AFirstValue";
	
	

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public TestObjectGuiModel(final String prefix, ModelConfiguration config) {
		super(config.ifNotSet(()->	GuiModel.createErrorProperty(prefix + "TestObject-Error", config)));
		aSecondValueProperty = new IntProperty(prefix + ASECOND_VALUE, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(TestObject::getASecondValue, TestObject::setASecondValue)),
			implicitConverters(TestObject.class, ASECOND_VALUE, java.lang.Integer.class));
		aFirstValueProperty = new ObjectProperty<java.lang.String>(prefix + AFIRST_VALUE, this).configureTyped(
			Configuration.persistent(currentObjectProvider, Persisters.getSet(TestObject::getAFirstValue, TestObject::setAFirstValue)),
			implicitConverters(TestObject.class, AFIRST_VALUE, java.lang.String.class));
		
		allProperties = new AbstractProperty[]{aSecondValueProperty, aFirstValueProperty};
    }
            
    public TestObjectGuiModel(ModelConfiguration config) {
    	this("", config);
    }

	public Class<?> getContainerClass() {
		return TestObject.class;
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
    		getPropertySupport().transmitAllToComponentOnly();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			getPropertySupport().enableAllTransmit();
		}
    }

    @Override
    public void save() {
		Arrays.stream(allProperties).forEach(AbstractProperty::save);
    }

    @Override
    public void setCurrentObject(final TestObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<TestObject> loadBinding() {
        return new IComponentBinding<>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<TestObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final TestObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
