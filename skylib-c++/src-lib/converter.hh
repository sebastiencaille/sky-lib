/*
 *Copyright (c) 2013 Sebastien Caille.
 *All rights reserved.
 *
 *Redistribution and use in source and binary forms are permitted
 *provided that the above copyright notice and this paragraph are
 *duplicated in all such forms and that any documentation,
 *advertising materials, and other materials related to such
 *distribution and use acknowledge that the software was developed
 *by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *from this software without specific prior written permission.
 *THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
/*
 * Converter.hh
 *
 *  Created on: Feb 19, 2012
 *      Author: scaille
 */

#ifndef CONVERTER_HH_
#define CONVERTER_HH_

#include <string>
#include <iostream>
#include <property_listener.hh>
#include <typed_property.hh>
#include "binding_link.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

/**
 * Base of every Converter
 */
template<class _FT, class _TT> class binding_converter:
		public abstract_binding_link<_FT, _TT> {

protected:

	virtual _TT convert_property_value_to_component_value(_FT propertyValue)= 0;

	virtual _FT convert_component_value_to_property_value(_TT componentValue) = 0;

	binding_converter() {
	}

	virtual ~binding_converter() {
	}

	void set_value_from_property(property* source, _FT value) {
		_TT converted = convert_property_value_to_component_value(value);
		this->m_bindingTo->set_to_value(source, converted);
	}

	/**
	 * Called by binding to set the value provided by the component
	 *
	 * @param source
	 * @param componentValue
	 */
	void set_value_from_component(void* _source, _TT _componentValue) {
		try {
			this->m_bindingFrom->set_from_value(_source, convert_component_value_to_property_value(_componentValue));
		} catch (void* e) {
			this->m_errorNotifier->set_error(this->m_bindingTo->get_component(), e);
		}
	}

};

}

#endif /* CONVERTER_HH_ */
