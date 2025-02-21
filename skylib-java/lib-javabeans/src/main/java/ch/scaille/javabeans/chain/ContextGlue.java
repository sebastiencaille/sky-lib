package ch.scaille.javabeans.chain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.util.dao.metadata.IAttributeMetaData;
import ch.scaille.util.dao.metadata.RecordMetaData;

public class ContextGlue implements PropertyChangeListener {

	private List<AbstractProperty> propertiesToRefresh = new ArrayList<>();

	public ContextGlue(Object context, AbstractProperty property) {
		propertiesToRefresh.add(property);
		install(context);
	}

	private void install(Object context) {
		if (context instanceof AbstractProperty) {
			((AbstractProperty) context).addListener(this);
		} else {
			final var metaData = new RecordMetaData<>((Class<Object>) context.getClass());
			metaData.getAttributes().stream().filter(attrib -> attrib.isOfType(AbstractProperty.class))
					.forEach(attrib -> install(context, attrib));
		}
	}

	private void install(Object context, IAttributeMetaData<Object> attrib) {
		AbstractProperty.class.cast(attrib.getValueOf(context)).addListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertiesToRefresh.forEach(prop -> prop.fireArtificialChange(evt.getSource()));
	}

}
