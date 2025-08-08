package ch.scaille.example.gui;

import static ch.scaille.javabeans.properties.ContextProperties.ofProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.IntProperty;
import ch.scaille.javabeans.properties.LongProperty;

class PropertiesTest {

	@Test
	void testRecord() {

		final var accumulator = new long[] {0}; 
		final var changeSupport = PropertyChangeSupportController.mainGroup(this);
		final var p1 = new IntProperty("p1", changeSupport);
		final var p2 = new LongProperty("p2", changeSupport);

		p1.bind(ofProperty(p2), (v, cp2) ->  v + cp2.getValue()).listen(v -> accumulator[0] = v);
		
		changeSupport.transmitChangesBothWays();
		
		p1.setValue(this, 1);
		assertEquals(1, accumulator[0]);

		p2.setValue(this, 1000);
		assertEquals(1001, accumulator[0]);
	}
	
	@Test
	void testRecordUpdated() {

		final var accumulator = new long[] {0}; 
		final var changeSupport = PropertyChangeSupportController.mainGroup(this);
		final var p1 = new IntProperty("p1", changeSupport);
		final var p2 = new LongProperty("p2", changeSupport);

		final var valueSenderHolder = new Runnable[1];
		p1.bind(ofProperty(p2), (v, cp2) -> v + cp2.getValue(), (v, context) -> v.intValue())
			.bind(new IComponentBinding<>() {
				@Override
				public void setComponentValue(final IComponentChangeSource source, final Long value) {
					accumulator[0] = value;	
				}

				@Override
				public void addComponentValueChangeListener(final IComponentLink<Long> link) {
					valueSenderHolder[0] = () -> link.setValueFromComponent("test", 2L);
				}


				@Override
				public void removeComponentValueChangeListener() {
					// noop
				}
			});
					
				
		
		changeSupport.transmitChangesBothWays();
		
		p1.setValue(this, 1);
		assertEquals(1, accumulator[0]);

		valueSenderHolder[0].run();
		assertEquals(2, p1.getValue());
	}

}

