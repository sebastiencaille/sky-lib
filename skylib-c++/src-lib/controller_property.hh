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
#include "converter_interface.hh"
#include "binding_chain.hh"

namespace org_skymarshall_util_hmi {

/**
 * Property intended to be used with a controller.<p>
 * Basically includes an error property.
 */
template<class _PT> class controller_property: public typed_property<_PT> {
private:
	_PT m_value;
	error_notifier* m_errorNotifier;

public:

	controller_property(const string& _name, property_manager& _manager,
			_PT _defaultValue, error_notifier* _errorNotifier) :
			typed_property<_PT>(_name, _manager, _defaultValue), m_errorNotifier(
					_errorNotifier) {
	}

	void attach() {
		// todo
	}

	template<class _NT> binding_chain<_PT>::end_of_chain<_NT>* bind(
			binding_converter<_PT, _NT>* _converter) {
		binding_chain<_PT>* chain = new binding_chain<_PT>(*this);
		return chain->bindProperty(
				property_setter_func_type<typed_property<_PT>, _PT>(this,
						&typed_property<_PT>::set))->bind(_converter);
	}

	template<class _Ct> void* bind(component_binding<_Ct>* _componentBinding) {
		binding_chain<_PT>* chain = new binding_chain<_PT>(*this);
		return chain->bindProperty(
				property_setter_func_type<typed_property<_PT>, _PT>(this,
						&typed_property<_PT>::set))->bind(_componentBinding);
	}

};
}
#endif /* CONTROLLERPROPERTY_HH_ */
