/*
 * binding_chain.hh
 *
 *  Created on: Sep 18, 2017
 *      Author: scaille
 */

#ifndef BINDING_CHAIN_HH_
#define BINDING_CHAIN_HH_

#include<vector>

#include <property.hh>
#include <property_listener.hh>

#include <converter_interface.hh>

namespace org_skymarshall_util_hmi {

using namespace std;

template<class _Tt> class binding_backward {
public:
	virtual void to_property(int index, const void* component, const _Tt value)
			throw (conversion_exception) = 0;
	virtual ~binding_backward() {
	}

};

template<class _Ft> class binding_forward {
public:
	virtual void to_component(int index, const _Ft value)
			throw (conversion_exception) = 0;
	virtual ~binding_forward() {
	}

};

class binding_storage {
public:
	void* m_binding_forward;
	void* m_binding_backward;
	binding_storage(void* _binding_forward, void* _binding_backward) :
			m_binding_forward(_binding_forward), m_binding_backward(
					_binding_backward) {
	}

	~binding_storage() {
	}
};

template<class _Ft, class _Tt> class abstract_binding_link: public binding_forward<
		_Ft>,
		public binding_backward<_Tt> {
public:

	virtual void handle_exception(const conversion_exception& e) = 0;

	virtual ~abstract_binding_link() {
	}
};

/** End of chain */

/** Binding chain */

template<class C, class _Pt> class property_setter_func_type {
public:
	typedef void (C::*setter_function)(const void*, const _Pt);
private:
	C* m_obj;
	setter_function m_setter_func;
public:
	property_setter_func_type(C* _obj, setter_function _setter_func) :
			m_obj(_obj), m_setter_func(_setter_func) {
	}

	void set(const void* _source, _Pt _newValue) {
		(m_obj->*m_setter_func)(_source, _newValue);
	}
};

template<class _Pt> class binding_chain {

private:
	vector<binding_storage*> m_links;

	property& m_property;

	property_listener_func_type<binding_chain> m_valueUpdateListener;

	bool m_transmit = true;

	//private ErrorNotifier errorNotifier;

	//private List<IBindingChainDependency> dependencies = new ArrayList<>();

	/**
	 * Endpoint connected to a property
	 */
	class property_link: public abstract_binding_link<_Pt, _Pt> {
		binding_chain& m_chain;
		property_setter_func_type<typed_property<_Pt>, _Pt> m_setter;
	public:
		property_link(binding_chain& _chain,
				property_setter_func_type<typed_property<_Pt>, _Pt> _setter) :
				m_chain(_chain), m_setter(_setter) {
		}

		void to_property(int _index, const void* _component, const _Pt _value)
				throw (conversion_exception) {
			m_setter.set(_component, _value);
		}

		void to_component(int _index, const _Pt _value)
				throw (conversion_exception) {
			((binding_forward<_Pt>*) m_chain.m_links[_index + 1]->m_binding_forward)->to_component(
					_index + 1, _value);
		}

		void handle_exception(const conversion_exception& e) {
			// no conversion
		}

		~property_link() {

		}
	};

	template<class _Ft, class _Tt> class converter_link: public abstract_binding_link<
			_Ft, _Tt> {
		binding_chain& m_chain;
		binding_converter<_Ft, _Tt>* m_converter;
	public:
		converter_link(binding_chain& _chain,
				binding_converter<_Ft, _Tt>* _converter) :
				m_chain(_chain), m_converter(_converter) {
		}

		void to_property(int _index, const void* _component, const _Tt _value)
				throw (conversion_exception) {
			((binding_backward<_Ft>*) m_chain.m_links[_index - 1]->m_binding_backward)->to_property(
					_index - 1, _component,
					m_converter->convert_component_value_to_property_value(
							_value));
		}

		void to_component(int _index, const _Ft _value)
				throw (conversion_exception) {

			((binding_forward<_Tt>*) m_chain.m_links[_index + 1]->m_binding_forward)->to_component(
					_index + 1,
					m_converter->convert_property_value_to_component_value(
							_value));
		}

		void handle_exception(const conversion_exception& e) {
			// no conversion
		}

		~converter_link() {

		}
	};

	template<class _Ct> class component_link_impl: public component_link<_Ct> {
		binding_chain& m_chain;

		component_binding<_Ct>* m_componentBinding;

	public:
		component_link_impl(binding_chain& _chain,
				component_binding<_Ct>* _componentBinding) :
				m_chain(_chain), m_componentBinding(_componentBinding) {
		}

		void set_value_from_component(void* component, _Ct componentValue) {
			if (!m_chain.m_transmit) {
				return;
			}
			const int lastIndex = m_chain.m_links.size() - 1;
			((binding_backward<_Ct>*) m_chain.m_links[lastIndex]->m_binding_backward)->to_property(
					lastIndex, component, componentValue);
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

	template<class _Ct> class chain_component_link: public abstract_binding_link<
			_Ct, _Ct> {

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

		void to_component(int index, _Ct value) throw (conversion_exception) {
			m_componentBinding->set_component_value(m_chain.m_property, value);
		}

		void to_property(int index, const void* component, const _Ct value)
				throw (conversion_exception) {
			((binding_backward<_Ct>*) m_chain.m_links[index - 1]->m_binding_backward)->to_property(
					index - 1, component, value);
		}

		void handle_exception(const conversion_exception& e) {
			// nope
		}
	};

	void propagate_property_change(const void* property, const string& name,
			const void* old_value, const void* new_value) {
		if (!m_transmit) {
			return;
		}
		((binding_forward<_Pt>*) m_links[0]->m_binding_forward)->to_component(0,
				*(_Pt*) new_value);
	}
public:
	binding_chain(property& c_property) :
			m_property(c_property), m_valueUpdateListener(this,
					&binding_chain::propagate_property_change) {
	}

	template<class _T> class end_of_chain;

	end_of_chain<_Pt>* bindProperty(
			property_setter_func_type<typed_property<_Pt>, _Pt> _setter) {
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

	template<class _Ct> component_link<_Ct>* bind(
			component_binding<_Ct>* _componentBinding) {
		chain_component_link<_Ct>* const link = new chain_component_link<_Ct>(
				*this, _componentBinding);
		m_links.push_back(
				new binding_storage(
						(void*) static_cast<binding_forward<_Ct>*>(link),
						(void*) static_cast<binding_backward<_Ct>*>(link)));
		return link->m_componentLink;
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

		template<class _Ct> component_link<_Ct>* bind(
				component_binding<_Ct>* _componentBinding) {
			// delete itself ?
			return m_bindingChain.bind(_componentBinding);
		}

		~end_of_chain() {
		}

	};

private:

	~binding_chain() {
		m_property.remove_listener(&m_valueUpdateListener);
		for (int i = 0; i < m_links.size(); i++) {
			delete m_links[i];
		}
		m_links.clear();
	}
};

}

#endif /* BINDING_CHAIN_HH_ */
