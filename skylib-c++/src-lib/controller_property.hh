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

#include "binding_interface.hh"
#include "typed_property.hh"
#include "binding_chain.hh"

namespace ch_skymarshall::gui {

using namespace std::placeholders;
/**
 * Property intended to be used with a controller.<p>
 * Basically includes an error property.
 */
template<class _Pt> class controller_property: public typed_property<_Pt> {
private:
	_Pt m_value;
	shared_ptr<error_notifier> m_errorNotifier;

public:

	controller_property(const string &_name, property_manager &_manager,
			_Pt _defaultValue, shared_ptr<error_notifier> _errorNotifier) :
			typed_property<_Pt>(_name, _manager, _defaultValue), m_errorNotifier(
					_errorNotifier) {
	}

	template<class _Cst> shared_ptr<end_of_chain<_Pt, _Cst>> bind(
			shared_ptr<binding_converter<_Pt, _Cst>> const _converter) {
		return binding_chain<_Pt>::of(*this, m_errorNotifier)->bind_property(
				std::bind(&controller_property::set, this, _1, _2))->bind(
				_converter);

	}

	template<class _Cst> shared_ptr<binding_chain_controller> bind(
			shared_ptr<component_binding<_Cst>> const _componentBinding) {
		return binding_chain<_Pt>::of(*this, m_errorNotifier)->bind_property(
				std::bind(&controller_property::set, this, _1, _2))->bind(
				_componentBinding);
	}

};
}

#endif /* CONTROLLERPROPERTY_HH_ */
