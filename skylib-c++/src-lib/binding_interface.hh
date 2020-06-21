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

#ifndef BINDINGINTERFACE_HH_
#define BINDINGINTERFACE_HH_

#include <string>
#include <functional>

#include "types.hh"
#include "typed_property.hh"

namespace ch_skymarshall::gui {

using namespace std;

class error_notifier {
	protected:
	virtual ~error_notifier() = default;

public:
	virtual void set_error(source_ptr _source, const gui_exception& _e) = 0;

	virtual void clear_error(source_ptr _source) = 0;
};

/**
 * Converters
 */

template<class _Pt, class _Ct> class binding_converter {
public:
	virtual const _Pt convert_component_value_to_property_value(
			const _Ct _componentValue) = 0;
	virtual const _Ct convert_property_value_to_component_value(
			const _Pt _propertyValue) = 0;

	virtual ~binding_converter() = default;

};

class logic_error_to_string: public binding_converter<gui_exception_ptr, string> {

public:
	const gui_exception_ptr convert_component_value_to_property_value(
			const string _componentValue) {
		// nonsense
		return NULL;
	}

	const string convert_property_value_to_component_value(
			gui_exception_ptr _propertyValue) {
		if (_propertyValue == NULL) {
			return string();
		}
		return string(_propertyValue->what());
	}

};

/** Link from component binding to chain */

template<class _CT> class component_link {
public:
	virtual void set_value_from_component(source_ptr _component,
			_CT _componentValue) = 0;
	virtual void reload_component_value() = 0;
	virtual void unbind() = 0;
protected:
	~component_link() {
	}

};

/**
 * Definition of component bindings
 */
template<class _CT> class component_binding {

public:

	virtual void add_component_value_change_listener(
			component_link<_CT>* _converter) = 0;

	virtual void remove_component_value_change_listener() = 0;

	virtual void set_component_value(property& _source, _CT _value) = 0;

	virtual source_ptr get_component() = 0;

	virtual ~component_binding() {
	}
};

class binding_chain_dependency;

class binding_chain_controller {
public:
	virtual void attach() = 0;

	virtual void detach() = 0;

	virtual property& get_property() = 0;

	virtual void unbind() = 0;

	virtual binding_chain_controller* add_dependency(
			binding_chain_dependency* dependency) = 0;

	virtual ~binding_chain_controller() {

	}
};

using namespace std::placeholders;

class binding_chain_dependency {
public:
	virtual void register_dep(binding_chain_controller* chain) = 0;

	virtual void unbind() = 0;

	virtual ~binding_chain_dependency() {
	}
};

/** Actions */

enum property_group_actions {
	BEFORE_FIRE, AFTER_FIRE
};

class action {
public:
	virtual ~action() {
	}

	virtual void apply(property_group_actions _action,
			const property* _property) = 0;

};

template<class _T> class action_dependency: public binding_chain_dependency {
private:
	property* m_targetProperty;
	std::function<
			void(property_group_actions _action, const property* _property)> m_action;
	property_listener_dispatcher* m_listener = NULL;

	void action_before(const source_ptr caller, property* _property) {
		m_action(BEFORE_FIRE, _property);
	}

	void action_after(const source_ptr caller, property* _property) {
		m_action(AFTER_FIRE, _property);
	}

public:

	action_dependency(property* _targetProperty,
			std::function<
					void(property_group_actions _action,
							const property* _property)> _action) :
			m_targetProperty(_targetProperty), m_action(_action) {
	}

	void register_dep(binding_chain_controller* _chain) {
		m_listener = new property_listener_dispatcher(
				std::bind(&action_dependency::action_before, this, _1, _2),
				std::bind(&action_dependency::action_after, this, _1, _2));
		m_targetProperty->add_listener(m_listener);
	}

	virtual void unbind() {
		m_targetProperty->remove_listener(m_listener);
	}

	virtual ~action_dependency() {
	}
};
}

#endif /* BINDINGINTERFACE_HH_ */
