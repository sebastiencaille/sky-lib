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
 * ControllerProperty.hh
 *
 *  Created on: Mar 4, 2012
 *      Author: scaille
 */

#ifndef CONTROLLERPROPERTY_HH_
#define CONTROLLERPROPERTY_HH_

#include <typed_property.hh>
#include <converter_interface.hh>
#include "converter.hh"

namespace org_skymarshall_util_hmi {

/**
 * Converter between types of same object
 */
template<class _Tp> class identity_converter:
		public binding_converter<_Tp, _Tp> {

protected:
	_Tp convertComponentValueToPropertyValue(const _Tp& _componentValue)/*			throws ConversionException*/{
		return _componentValue;
	}

	_Tp convertPropertyValueToComponentValue(const _Tp& _propertyValue) {
		return _propertyValue;
	}

public:
	identity_converter(component_binding<_Tp>* _binding, bool _bindingDeletedOnDelete) :
					binding_converter<_Tp, _Tp>(_binding, _bindingDeletedOnDelete) {
	}
};

/**
 * Property intended to be used with a controller.<p>
 * Basically includes an error property.
 */
template<class _PT> class controller_property:
		public typed_property<_PT> {
private:
	_PT m_value;
	error_notifier* m_errorNotifier;

public:

	controller_property(const string& _name, property_manager& _manager, _PT _defaultValue,
			error_notifier* _errorNotifier) :
					typed_property<_PT>(_name, _manager, _defaultValue),
					m_errorNotifier(_errorNotifier) {
	}

	template<class _NT> typename abstract_binding_link<_PT, _NT>::binding_controller* bind(
			abstract_binding_link<_PT, _NT>* _converter) {
		return _converter->bind(this, m_errorNotifier);
	}

	void bind(component_binding<_PT>* _binding, bool _bindingDeletedOnDelete) {
		bind(new identity_converter<_PT>(_binding, _bindingDeletedOnDelete));
	}

};
}
#endif /* CONTROLLERPROPERTY_HH_ */
