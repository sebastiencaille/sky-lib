/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.gui.mvc;

import java.util.function.Consumer;
import java.util.function.Supplier;

import ch.skymarshall.gui.mvc.properties.AbstractTypedProperty;
import ch.skymarshall.gui.mvc.properties.ErrorProperty;

public class GuiModel {

	public static class ModelConfiguration {
		protected final IScopedSupport propertySupport;
		protected ErrorProperty errorProperty;
		

		public ModelConfiguration(IScopedSupport propertySupport) {
			this.propertySupport = propertySupport;
		}

		public ModelConfiguration with(ErrorProperty errorProperty) {
			this.errorProperty = errorProperty;
			return this;
		}
		
		public ModelConfiguration ifNotSet(Supplier<ErrorProperty> errSupplier) {
			if (this.errorProperty == null) {
				this.errorProperty = errSupplier.get();
			}
			return this;
		}

		public ModelConfiguration validate() {
			if (errorProperty == null) {
				errorProperty = createErrorProperty("InputError", this);
			}
			return this;
		}

		public IScopedSupport getPropertySupport() {
			return propertySupport;
		}

		public ErrorProperty getErrorProperty() {
			return errorProperty;
		}

	}

	public static ModelConfiguration with(final IScopedSupport propertySupport, final ErrorProperty errorProperty) {
		return new ModelConfiguration(propertySupport).with(errorProperty);
	}

	public static ModelConfiguration with(final IScopedSupport propertySupport) {
		return new ModelConfiguration(propertySupport);
	}

	public static ModelConfiguration of(final GuiController controller) {
		return new ModelConfiguration(controller.getScopedChangeSupport());
	}

	protected final ModelConfiguration configuration;

	public GuiModel(ModelConfiguration configuration) {
		this.configuration = configuration.validate();
	}

	public ModelConfiguration getConfiguration() {
		return configuration;
	}

	public ErrorProperty getErrorProperty() {
		return configuration.errorProperty;
	}

	protected static ErrorProperty createErrorProperty(final String name, final ModelConfiguration config) {
		return new ErrorProperty(name, config.getPropertySupport());
	}

	public IScopedSupport getPropertySupport() {
		return configuration.propertySupport;
	}

	public void activate() {
		configuration.propertySupport.attachAll();
	}

}
