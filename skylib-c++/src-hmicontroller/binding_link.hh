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
 * binding_link.hh
 *
 *  Created on: Feb 3, 2013
 *      Author: scaille
 */

#ifndef BINDING_LINK_HH_
#define BINDING_LINK_HH_

#include "converter_interface.hh"

namespace org_skymarshall_util_hmi {

using namespace std;

/** A marker */
class anonymous_binding_controller {
public:
	virtual ~anonymous_binding_controller() {
	}
};

class binding_registry {

public:
	virtual ~binding_registry() {
	}
	virtual void register_binding(anonymous_binding_controller* binding) = 0;

};

template<class _FT, class _TT> class abstract_binding_link {

	typedef _FT from_type;
	typedef _TT to_type;

public:
	class binding_controller;

private:

	typedef abstract_binding_link<from_type, to_type> binding_link;

	class binding_from {
	public:
		~binding_from() {
		}
		virtual void set_from_value(void* source, from_type value) = 0;

		virtual typed_property<from_type>* get_property() = 0;

		virtual from_type get_value() = 0;
	};

	class binding_to {
	public:
		~binding_to() {
		}

		virtual void set_to_value(property* source, to_type value) = 0;

		virtual void* get_component() = 0;
	};

	class binding_to_component:
			public binding_to,
			public converter_to<to_type> {

		binding_link* m_chain;
		component_binding<to_type>* m_componentBinding;

		friend abstract_binding_link;

		binding_to_component(binding_link* _chain, component_binding<to_type>* _componentBinding) :
						m_chain(_chain),
						m_componentBinding(_componentBinding) {
		}

		~binding_to_component() {
		}

	public:
		void* get_component() {
			return m_componentBinding->get_component();
		}

		void set_to_value(property* source, to_type value) {
			m_componentBinding->set_component_value(source, value);
		}

		/**
		 * Called by binding to set the value provided by the component
		 *
		 * @param source
		 * @param componentValue
		 */

		void set_value_from_component(void * _source, to_type& _componentValue) {
			m_chain->set_value_from_component(_source, _componentValue);
		}

		binding_to* bind() {
			m_componentBinding->add_component_value_change_listener(this);
			return this;
		}
	};

	template<class _NT> class binding_to_link:
			binding_to {

		typedef _NT next_type;

		friend abstract_binding_link;

		typedef typename abstract_binding_link<to_type, next_type>::binding_controller next_binding;

		binding_link* m_chain;
		abstract_binding_link<to_type, next_type>* m_next;

		binding_to_link(binding_link* _chain, abstract_binding_link<to_type, next_type>* _next) :
						m_chain(_chain),
						m_next(_next) {
		}

		~binding_to_link() {
		}

		next_binding* bind() {
			return m_next->bind_with_link(m_chain, m_chain->m_errorNotifier);
		}

		void set_to_value(property* source, to_type value) {
			m_next->set_value_from_property(source, value);
		}

		void* get_component() {
			return m_next->getComponent();
		}

	};

public:

	class binding_controller;

	/**
	 * Controls behavior of the link
	 */
	class binding_controller:
			public anonymous_binding_controller {
	protected:
		binding_link* m_link;

		binding_controller(binding_link* _link) :
						m_link(_link) {
		}

	public:
		void bind(component_binding<to_type>* newBinding) {
			cout << "Binding comp " << hex << this << " of " << hex << m_link << " to " << hex << newBinding << endl;
			if (m_link->m_bindingTo != NULL) {
				cout << "bound to " << m_link->m_bindingTo << endl;
				throw new string("Already bound"/* + bindingTo*/);
			}
			binding_to_component* binding = new binding_to_component(m_link, newBinding);
			m_link->m_bindingTo = binding->bind();
		}

		template<class NT> typename abstract_binding_link<to_type, NT>::binding_controller* bind(
				abstract_binding_link<to_type, NT>* link) {
//			cout << "Binding link " << hex << this << " to " << hex << link << endl;
			binding_to_link<NT>* binding = new binding_to_link<NT>(m_link, link);
			m_link->m_bindingTo = binding;
			return binding->bind();
		}

		typed_property<from_type>* get_property() {
			return m_link->m_bindingFrom->get_property();
		}

		binding_controller* register_binding(binding_registry* registry) {
			registry->register_binding(this);
			return this;
		}

		~binding_controller() {
			if (m_link != NULL) {
				delete m_link;
			}
		}
	};

	/**
	 * Allows to prevent transmitting value set into component
	 *
	 * @author scaille
	 *
	 */
	class property_binding_controller:
			public binding_controller,
			public binding_from {

		friend abstract_binding_link;

		typed_property<from_type>* m_property;

		from_type m_detachedValue;

		property_listener_func_type<property_binding_controller> m_listener;

		property_binding_controller(binding_link* _link, typed_property<from_type>* _property) :
						binding_controller(_link),
						m_property(_property),
						m_listener(this, &property_binding_controller::propertyChanged) {
		}

		void propertyChanged(const void*, const string&, const void*, const void*) {
			if (this->m_link->m_transmit) {
				this->m_link->set_value_from_property(m_property, this->m_link->m_bindingFrom->get_value());
			}
		}

		typed_property<from_type>* get_property() {
			return m_property;
		}
	public:
		~property_binding_controller() {
		}

		property_binding_controller* bind() {
			m_property->add_listener(&m_listener);
			return this;
		}

		void unbind() {
			m_property->remove_listener(&m_listener);
		}

		void set_from_value(void* source, from_type value) {
			m_property->set(source, value);
		}

		from_type get_value() {
			return m_property->get();
		}

		void attach() {
			this->m_link->m_transmit = true;
			m_property->set(this->m_link->bindingTo.getComponent(), m_detachedValue);
		}

		void detach() {
			this->m_link->m_transmit = false;
			m_detachedValue = this->m_link->get_property_value();
		}

	};

	/**
	 * Forwards value set from a link.
	 *
	 * @author scaille
	 *
	 */
	template<class _PT> class link_binding_controller:
			public binding_controller,
			protected binding_from {

		typedef _PT previous_type;

		friend abstract_binding_link;

		typedef abstract_binding_link<previous_type, from_type> previous_binding;

		previous_binding* m_prev;
		link_binding_controller(binding_link* _link, previous_binding* _prev) :
						binding_controller(_link),
						m_prev(_prev) {
		}

		~link_binding_controller() {
		}
	public:

		void attach() {
		}

		void detach() {
		}

		void unbind() {
		}

		property get_property() {
			return m_prev->get_property();
		}

		from_type get_value() {
			return m_prev->get_value();
		}

		void set_from_value(void* source, from_type value) {
			this->m_link->set_value_from_component(source, value);
		}
	};

protected:

	binding_from* m_bindingFrom;

	binding_to* m_bindingTo;

	error_notifier* m_errorNotifier;

	bool m_transmit;

	virtual void set_value_from_property(property* source, from_type value) = 0;

	virtual void set_value_from_component(void* source, to_type componentValue) = 0;

public:
	property get_property() {
		return m_bindingFrom->get_property();
	}

	abstract_binding_link() :
					m_bindingFrom(NULL),
					m_bindingTo(NULL),
					m_errorNotifier(NULL),
					m_transmit(true) {
	}

	virtual ~abstract_binding_link() {
		if (m_bindingTo != NULL) {
			delete m_bindingTo;
		}
	}

	binding_controller* bind(typed_property<from_type>* _property, error_notifier* _errorNotifier) {
		m_errorNotifier = _errorNotifier;
		property_binding_controller* controller = new property_binding_controller(this, _property);
		m_bindingFrom = controller;
		return controller->bind();
	}

	template<class _PT> binding_controller* bind(abstract_binding_link<_PT, from_type>* _link,
			error_notifier* _errorNotifier) {
		m_errorNotifier = _errorNotifier;
		link_binding_controller<_PT>* controller = new link_binding_controller<_PT>(_link, _errorNotifier);
		m_bindingFrom = controller;
		return controller->bind();
	}

	void* get_component() {
		return m_bindingTo->get_component();
	}

	void reload_component_value() {
		set_value_from_property(m_bindingFrom->get_property(), m_bindingFrom->get_value());
	}

	binding_controller* listen_to_property(property _property) {
		property_binding_controller* controller = new property_binding_controller(this, _property);
		return controller->bind();
	}
};

}

#endif /* BINDING_LINK_HH_ */
