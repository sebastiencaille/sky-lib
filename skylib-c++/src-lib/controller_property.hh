
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

	controller_property(const string_view &_name, property_manager &_manager,
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

	~controller_property() override = default;

};
}

#endif /* CONTROLLERPROPERTY_HH_ */
