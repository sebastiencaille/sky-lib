// File generated from template
package org.skymarshall.example.hmi;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.Properties;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.persisters.GetSetAccess;
import org.skymarshall.hmi.mvc.properties.IntProperty;


public class TestObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.TestObject> {
   
    private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

	public static final String ASECOND_VALUE = "ASecondValue";
	
	public static final String AFIRST_VALUE = "AFirstValue";
	
	

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;
	

    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport, final ErrorProperty errorProperty) {
        super(propertySupport, errorProperty);
		aSecondValueProperty = Properties.of(new IntProperty(prefix + "-ASecondValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.TestObject,java.lang.Integer>access((o) -> o::getASecondValue, (o) ->o::setASecondValue))).setErrorNotifier(errorProperty).getProperty();
		aFirstValueProperty = Properties.of(new ObjectProperty<java.lang.String>(prefix + "-AFirstValue",  propertySupport)).persistent(Persisters.from(currentObjectProvider, GetSetAccess.<org.skymarshall.example.hmi.TestObject,java.lang.String>access((o) -> o::getAFirstValue, (o) ->o::setAFirstValue))).setErrorNotifier(errorProperty).getProperty();
		
    }

    public TestObjectHmiModel(final String prefix, final HmiController controller) {
        this(prefix, controller.getPropertySupport(), HmiModel.createErrorProperty(prefix + "-Error", controller.getPropertySupport()));
    }

    public TestObjectHmiModel(final HmiController controller) {
        this("TestObject", controller.getPropertySupport(), HmiModel.createErrorProperty("TestObject-Error", controller.getPropertySupport()));
    }

    public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport) {
        this(prefix, propertySupport, HmiModel.createErrorProperty(prefix + "-Error", propertySupport));
    }

    public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
        this("TestObject", propertySupport, HmiModel.createErrorProperty("TestObject-Error", propertySupport));
    }


	public IntProperty getASecondValueProperty() {
	    return aSecondValueProperty;
	}
	public ObjectProperty<java.lang.String> getAFirstValueProperty() {
	    return aFirstValueProperty;
	}
	

    @Override
    public void load() {
		aSecondValueProperty.load(this);aFirstValueProperty.load(this);
    }

    @Override
    public void save() {
		aSecondValueProperty.save();aFirstValueProperty.save();
    }

    @Override
    public void setCurrentObject(final org.skymarshall.example.hmi.TestObject value) {
        currentObjectProvider.setObject(value);
    }

    public IComponentBinding<org.skymarshall.example.hmi.TestObject> binding() {
        return new IComponentBinding<org.skymarshall.example.hmi.TestObject>() {
        
            @Override
            public void addComponentValueChangeListener(final IComponentLink<org.skymarshall.example.hmi.TestObject> link) {
                // nope
            }
            
            @Override
			public void removeComponentValueChangeListener() {
				  // nope
			}
            
            @Override
            public void setComponentValue(final AbstractProperty source, final org.skymarshall.example.hmi.TestObject value) {
                if (value != null) {
                    setCurrentObject(value);
                    load();
                }
            }
        };
    }
}
