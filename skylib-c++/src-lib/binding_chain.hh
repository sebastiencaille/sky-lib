/*
 * binding_chain.hh
 *
 *  Created on: Sep 18, 2017
 *      Author: scaille
 */

#ifndef BINDING_CHAIN_HH_
#define BINDING_CHAIN_HH_

#include <vector>
#include <functional>

#include "binding_interface.hh"
#include "property.hh"
#include "property_listener.hh"

namespace org_skymarshall_util_hmi {

using namespace std;
using namespace std::placeholders;

template<class _Tt> class binding_backward {
public:
	virtual void to_property(int index, const source_ptr component,
			const _Tt value) = 0;
	virtual ~binding_backward() = default;
};

template<class _Ft> class binding_forward {
public:
	virtual void to_component(int index, const _Ft value) = 0;
	virtual ~binding_forward() = default;
};

class binding_storage {
public:
	void* m_binding_forward;
	void* m_binding_backward;
	binding_storage(void* _binding_forward, void* _binding_backward) :
			m_binding_forward(_binding_forward), m_binding_backward(
					_binding_backward) {
	}

	~binding_storage() = default;
};

/** End of chain */

/** Binding chain */

template<class _Pt> class binding_chain: public binding_chain_controller {

private:
	vector<binding_storage*> m_links;

	property& m_property;

	error_notifier* m_errorNotifier;

	property_listener_dispatcher m_valueUpdateListener;

	bool m_transmit = true;

	typedef list<binding_chain_dependency*>::iterator binding_chain_dependency_iter;
	list<binding_chain_dependency*> m_dependencies;

	void attach() {
		m_transmit = true;
		m_property.attach();
	}

	void detach() {
		m_transmit = false;
	}

	property& get_property() {
		return m_property;
	}

	binding_chain_controller* add_dependency(
			binding_chain_dependency* _dependency) {
		m_dependencies.push_back(_dependency);
		_dependency->register_dep(this);
		return this;
	}

	void unbind() {
		for (binding_chain_dependency_iter iter = m_dependencies.begin();
				iter != m_dependencies.end(); iter++) {
			(*iter)->unbind();
		}
		m_dependencies.clear();
	}

	/**
	 * Link connected to a property (first of the list)
	 */
	class property_link: public binding_forward<_Pt>, public binding_backward<
			_Pt> {
		binding_chain& m_chain;
		std::function<void(source_ptr, _Pt)> m_setter;
	public:
		property_link(binding_chain& _chain,
				std::function<void(source_ptr, _Pt)> _setter) :
				m_chain(_chain), m_setter(_setter) {
		}

		void to_property(int _index, source_ptr _component, const _Pt _value) {
			m_setter(_component, _value);
		}

		void to_component(int _index, const _Pt _value) {
			((binding_forward<_Pt>*) m_chain.m_links[_index + 1]->m_binding_forward)->to_component(
					_index + 1, _value);
		}

		~property_link() {

		}
	};

	/** Link that convert some values */
	template<class _Ft, class _Tt> class converter_link: public binding_forward<
			_Ft>, public binding_backward<_Tt> {
		binding_chain& m_chain;
		binding_converter<_Ft, _Tt>* m_converter;
	public:
		converter_link(binding_chain& _chain,
				binding_converter<_Ft, _Tt>* _converter) :
				m_chain(_chain), m_converter(_converter) {
		}

		void to_property(int _index, source_ptr _component, const _Tt _value) {
			((binding_backward<_Ft>*) m_chain.m_links[_index - 1]->m_binding_backward)->to_property(
					_index - 1, _component,
					m_converter->convert_component_value_to_property_value(
							_value));
		}

		void to_component(int _index, const _Ft _value) {

			((binding_forward<_Tt>*) m_chain.m_links[_index + 1]->m_binding_forward)->to_component(
					_index + 1,
					m_converter->convert_property_value_to_component_value(
							_value));
		}

		~converter_link() {

		}
	};

	/**
	 * Interface given to the component's link, allowing the component binding to interact with the chain
	 */
	template<class _Ct> class component_link_impl: public component_link<_Ct> {
		binding_chain& m_chain;

		component_binding<_Ct>* m_componentBinding;

	public:
		component_link_impl(binding_chain& _chain,
				component_binding<_Ct>* _componentBinding) :
				m_chain(_chain), m_componentBinding(_componentBinding) {
		}

		void set_value_from_component(source_ptr _component,
				_Ct _componentValue) {
			if (!m_chain.m_transmit) {
				return;
			}
			const int lastIndex = m_chain.m_links.size() - 1;
			try {
				((binding_backward<_Ct>*) m_chain.m_links[lastIndex]->m_binding_backward)->to_property(
						lastIndex, _component, _componentValue);
			} catch (const hmi_exception& _e) {
				m_chain.m_errorNotifier->set_error(_component, _e);
			}
		}

		void unbind() {
			m_componentBinding->remove_component_value_change_listener();
		}

		void reload_component_value() {
			// should trigger the listeners
			m_chain.m_property.attach();
		}

		~component_link_impl() {
			delete m_componentBinding;
		}
	};

	template<class _Ct> class chain_component_link: public binding_forward<_Ct>,
			public binding_backward<_Ct> {

		friend class binding_chain;

		binding_chain& m_chain;
		component_binding<_Ct>* m_componentBinding;

	public:
		component_link<_Ct>* const m_componentLink;

		chain_component_link(binding_chain& _chain,
				component_binding<_Ct>* _newBinding) :
				m_chain(_chain), m_componentBinding(_newBinding), m_componentLink(
						new component_link_impl<_Ct>(_chain, _newBinding)) {
			m_componentBinding->add_component_value_change_listener(
					m_componentLink);
		}

		void to_component(int index, _Ct value) {
			m_componentBinding->set_component_value(m_chain.m_property, value);
		}

		void to_property(int index, source_ptr component, const _Ct value) {
			((binding_backward<_Ct>*) m_chain.m_links[index - 1]->m_binding_backward)->to_property(
					index - 1, component, value);
		}
	};

	void propagate_property_change(source_ptr _property, const string& _name,
			const void* _old_value, const void* _new_value) {
		if (!m_transmit) {
			return;
		}
		try {
			((binding_forward<_Pt>*) m_links[0]->m_binding_forward)->to_component(
					0, *(_Pt*) _new_value);
		} catch (const hmi_exception& _e) {
			m_errorNotifier->set_error(_property, _e);
		}
	}
public:
	binding_chain(property& _property, error_notifier* _notifier) :
			m_property(_property), m_errorNotifier(_notifier), m_valueUpdateListener(
					std::bind(&binding_chain::propagate_property_change, this,
							_1, _2, _3, _4)) {
	}

	template<class _T> class end_of_chain;

	end_of_chain<_Pt>* bind_property(
			std::function<void(source_ptr, _Pt)> _setter) {
		m_property.add_listener(&m_valueUpdateListener);
		property_link* const link = new property_link(*this, _setter);
		m_links.push_back(
				new binding_storage(
						(void*) static_cast<binding_forward<_Pt>*>(link),
						(void*) static_cast<binding_backward<_Pt>*>(link)));
		return new end_of_chain<_Pt>(*this);
	}

	template<class _FT, class _TT> end_of_chain<_TT>* bind(
			binding_converter<_FT, _TT>* _converter) {
		converter_link<_FT, _TT>* const link = new converter_link<_FT, _TT>(
				*this, _converter);
		m_links.push_back(
				new binding_storage(
						(void*) static_cast<binding_forward<_FT>*>(link),
						(void*) static_cast<binding_backward<_TT>*>(link)));
		return new end_of_chain<_TT>(*this);
	}

	template<class _Ct> binding_chain_controller* bind(
			component_binding<_Ct>* _componentBinding) {
		chain_component_link<_Ct>* const link = new chain_component_link<_Ct>(
				*this, _componentBinding);
		m_links.push_back(
				new binding_storage(
						(void*) static_cast<binding_forward<_Ct>*>(link),
						(void*) static_cast<binding_backward<_Ct>*>(link)));
		return this;
	}

	template<class _T> class end_of_chain {
	protected:
		binding_chain& m_bindingChain;
	public:
		end_of_chain(binding_chain& _bindingChain) :
				m_bindingChain(_bindingChain) {
		}

		template<class _NT> end_of_chain<_NT>* bind(
				binding_converter<_T, _NT>* converter) {
			// delete itself ?
			return m_bindingChain.bind<_T, _NT>(converter);
		}

		template<class _Ct> binding_chain_controller* bind(
				component_binding<_Ct>* _componentBinding) {
			// delete itself ?
			return m_bindingChain.bind(_componentBinding);
		}

		~end_of_chain() = default;

	};

private:

	~binding_chain() {
		m_property.remove_listener(&m_valueUpdateListener);
		for (unsigned int i = 0; i < m_links.size(); i++) {
			delete m_links[i];
		}
		m_links.clear();
	}
};

}

#endif /* BINDING_CHAIN_HH_ */
