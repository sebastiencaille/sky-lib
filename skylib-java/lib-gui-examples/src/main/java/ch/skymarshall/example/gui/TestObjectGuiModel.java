// File generated from template
package ch.skymarshall.example.gui;

import java.util.Arrays;
import ch.skymarshall.gui.mvc.GuiController;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.IObjectGuiModel;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.ErrorProperty;
import ch.skymarshall.gui.mvc.properties.Properties;
import ch.skymarshall.gui.mvc.persisters.GetSetAccess;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;


public class TestObjectGuiModel extends GuiModel implements IObjectGuiModel<ch.skymarshall.example.gui.TestObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

	public static final String ASECOND_VALUE = "ASecondValue";
	
	public static final String AFIRST_VALUE = "AFirstValue";
	
	

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;
	
	
	protected final AbstractProperty[] allProperties;
	
    public TestObjectGuiModel(final String prefix, final IScopedSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
		aSecondValueProperty = Properties.of(new IntProperty(prefix + "-ASecondValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.TestObject,java.lang.Integer>access(o -> o::getASecondValue, o -> o::setASecondValue))).setErrorNotifier(errorProperty).getProperty();
		aFirstValueProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-AFirstValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<ch.skymarshall.example.gui.TestObject,java.lang.String>access(o -> o::getAFirstValue, o -> o::setAFirstValue))).setErrorNotifier(errorProperty).getProperty();
		
		allProperties = new AbstractProperty[]{aSecondValueProperty, aFirstValueProperty};
    }

    public TestObjectGuiModel(final String prefix, final GuiController controller) {
        this(prefix, controller.getScopedChangeSupport(), GuiModel.createErrorProperty(prefix + "-TestObject-Error", controller.getScopedChangeSupport()));
    }

    public TestObjectGuiModel(final GuiController controller) {
        this("TestObject", controller.getScopedChangeSupport(), GuiModel.createErrorProperty("TestObject-Error", controller.getScopedChangeSupport()));
    }

    public TestObjectGuiModel(final String prefix, final IScopedSupport propertySupport) {
        this(prefix, propertySupport, GuiModel.createErrorProperty(prefix + "-TestObject-Error", propertySupport));
    }

    public TestObjectGuiModel(final IScopedSupport propertySupport) {
        this("TestObject", propertySupport, GuiModel.createErrorProperty("TestObject-Error", propertySupport));
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
    		propertySupport.transmitAllToComponentOnly();
			Arrays.stream(allProperties).forEach(p -> p.load(this));
		} finally {
			propertySupport.enableAllTransmit();
		}
    }

    @Override
    public void save() {
		Arrays.stream(allProperties).forEach(p -> p.save());
    }

    @Override
    public void setCurrentObject(final ch.skymarshall.example.gui.TestObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<ch.skymarshall.example.gui.TestObject> loadBinding() {
        return new IComponentBinding<ch.skymarshall.example.gui.TestObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<ch.skymarshall.example.gui.TestObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final ch.skymarshall.example.gui.TestObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
