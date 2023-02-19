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
#include <memory>

#include "utils.hh"
#include "binding_interface.hh"
#include "property.hh"
#include "property_listener.hh"

namespace ch_skymarshall::gui {

using namespace std;
using namespace std::placeholders;

/**
 * Interface to send component side type (_CsT) to the binding
 */
template<class _CsT> class binding_backward {
public:
	virtual void to_property(int index, const source_ptr component,
			const _CsT value) = 0;
	virtual ~binding_backward() = default;
};

/**
 * Interface to send property side type (_PsT) of the value
 */
template<class _PsT> class binding_forward {
public:
	virtual void to_component(int index, const _PsT value) = 0;
	virtual ~binding_forward() = default;
};

class binding_link {
public:
	virtual ~binding_link() = default;
};

template<class _Pt, class _PsT> class end_of_chain;

/** A whole binding chain, attached to the property, attaching to the component
 *
 * _Pt: Property type of the chain
 *
 */

template<class _Pt> class binding_chain: public binding_chain_controller {

private:
	template<typename T, typename U>
	friend class end_of_chain;

	vector<shared_ptr<binding_link>> m_links;

	property &m_property;

	shared_ptr<error_notifier> m_errorNotifier;

	weak_ptr<property_listener_dispatcher> m_valueUpdateListener;

	bool m_transmit = true;

	list<shared_ptr<binding_chain_dependency>> m_dependencies;

	weak_ptr<binding_chain_controller> m_myself;

public:

	static shared_ptr<binding_chain<_Pt>> of(property &_property,
			shared_ptr<error_notifier> _notifier) {
		auto me = make_shared<binding_chain<_Pt>>(_property, _notifier);
		me->m_myself = me;
		return me;
	}

	binding_chain(property &_property, shared_ptr<error_notifier> _notifier) :
			m_property(_property), m_errorNotifier(_notifier) {
	}

	void attach() final {
		m_transmit = true;
		m_property.attach();
	}

	void detach() final {
		m_transmit = false;
	}

	property& get_property() final {
		return m_property;
	}

	shared_ptr<binding_chain_controller> add_dependency(
			shared_ptr<binding_chain_dependency> _dependency) final {
		m_dependencies.push_back(_dependency);
		_dependency->register_dep(m_myself, _dependency);
		return m_myself.lock();
	}

	template<class _PsT> void to_property(int _index, source_ptr _component,
			_PsT value) {
		dynamic_cast<binding_backward<_PsT>*>(m_links[_index].get())->to_property(
				_index, _component, value);
	}

	template<class _CsT> void to_component(int _index, _CsT value) {
		dynamic_cast<binding_forward<_CsT>*>(m_links[_index].get())->to_component(
				_index, value);
	}

	/**
	 * Link connected to a property (first binding of the list)
	 */
	class property_link: public binding_link,
			public binding_forward<_Pt>,
			public binding_backward<_Pt> {
		binding_chain &m_chain;
		std::function<void(source_ptr, _Pt)> m_setter;
	public:
		property_link(binding_chain &_chain,
				std::function<void(source_ptr, _Pt)> _setter) :
				m_chain(_chain), m_setter(_setter) {
		}

		void to_property(int _index, source_ptr _component, const _Pt _value) {
			m_setter(_component, _value);
			m_chain.m_errorNotifier->clear_error(_component);
		}

		void to_component(int _index, const _Pt _value) {
			m_chain.to_component(_index + 1, _value);
		}

		~property_link() final = default;
	};

	/** Link that convert from _PsT type (property side) to _CsT type (component side) */
	template<class _PsT, class _CsT> class converter_link: public binding_link,
			public binding_forward<_PsT>,
			public binding_backward<_CsT> {

		binding_chain &m_chain;
		shared_ptr<binding_converter<_PsT, _CsT>> m_converter;
	public:
		converter_link(binding_chain &_chain,
				shared_ptr<binding_converter<_PsT, _CsT>> _converter) :
				m_chain(_chain), m_converter(_converter) {
		}

		void to_property(int _index, source_ptr _component, const _CsT _value) {
			try {
				_PsT converted_value =
						m_converter->convert_component_value_to_property_value(
								_value);
				m_chain.to_property(_index - 1, _component, converted_value);

			} catch (gui_exception &_e) {
				m_chain.m_errorNotifier->set_error(_component, _e);
			}
		}

		void to_component(int _index, const _PsT _value) {
			_CsT converted_value =
					m_converter->convert_property_value_to_component_value(
							_value);
			m_chain.to_component(_index + 1, converted_value);
		}

		~converter_link() final = default;
	};

	/**
	 * Link to connect to the component
	 */
	template<class _Ct> class chain_component_link: public binding_link,
			public binding_forward<_Ct>,
			public binding_backward<_Ct>,
			public component_link<_Ct> {

		friend class binding_chain;

		binding_chain &m_chain;
		shared_ptr<component_binding<_Ct>> m_componentBinding;

	public:

		static shared_ptr<chain_component_link<_Ct>> of(binding_chain &_chain,
				shared_ptr<component_binding<_Ct>> _newBinding) {
			auto me = make_shared<chain_component_link<_Ct>>(_chain,
					_newBinding);
			me->chain(me);
			return me;
		}

		chain_component_link(binding_chain &_chain,
				shared_ptr<component_binding<_Ct>> _newBinding) :
				m_chain(_chain), m_componentBinding(_newBinding) {

		}

		void chain(shared_ptr<chain_component_link<_Ct>> &_myself) {
			m_componentBinding->add_component_value_change_listener(_myself);
		}

		void to_component(int _index, _Ct value) {
			m_componentBinding->set_component_value(m_chain.m_property, value);
			if (m_chain.m_errorNotifier != nullptr) {
				m_chain.m_errorNotifier->clear_error(&m_chain.get_property());
			}
		}

		void to_property(int _index, source_ptr _component, const _Ct _value) {
			m_chain.to_property(_index - 1, _component, _value);
		}

		void set_value_from_component(source_ptr _component,
				_Ct _componentValue) {
			if (!m_chain.m_transmit) {
				return;
			}
			try {
				to_property(m_chain.m_links.size() - 1, _component,
						_componentValue);
			} catch (const gui_exception &_e) {
				m_chain.m_errorNotifier->set_error(_component, _e);
			}
		}

		void reload_component_value() {
			// should trigger the listeners
			m_chain.m_property.attach();
		}

		~chain_component_link() final {
			m_componentBinding->remove_component_value_change_listener();
		}

	};

	void propagate_property_change(source_ptr _property, const string_view &_name,
			const void *_old_value, const void *_new_value) {
		if (!m_transmit) {
			return;
		}
		try {
			to_component(0, *(const _Pt*) _new_value);
		} catch (const gui_exception &_e) {
			m_errorNotifier->set_error(_property, _e);
		}
	}

	template<class _PsT, class _CsT> shared_ptr<end_of_chain<_Pt, _CsT>> bind(
			shared_ptr<binding_converter<_PsT, _CsT>> _converter) {
		m_links.push_back(
				std::make_shared<converter_link<_PsT, _CsT>>(*this,
						_converter));
		return new_end_of_chain<_CsT>();
	}

	template<class _Ct> weak_ptr<binding_chain_controller> bind(
			shared_ptr<component_binding<_Ct>> _componentBinding) {
		m_links.push_back(
				chain_component_link<_Ct>::of(*this, _componentBinding));
		return m_myself;
	}

	template<class _ET> shared_ptr<end_of_chain<_Pt, _ET>> new_end_of_chain() {
		return make_shared<end_of_chain<_Pt, _ET>>(*this);
	}

	shared_ptr<end_of_chain<_Pt, _Pt>> bind_property(
			std::function<void(source_ptr, _Pt)> _setter) {
		m_property.add_listener(
				property_listener_dispatcher::ofLazy(m_valueUpdateListener,
						m_myself,
						std::bind(&binding_chain::propagate_property_change,
								this, _1, _2, _3, _4)));
		m_links.push_back(std::make_shared<property_link>(*this, _setter));
		return new_end_of_chain<_Pt>();
	}

	~binding_chain() final {
		DESTR_LOG("~binding_chain");
		if (!m_valueUpdateListener.expired()) {
			m_property.remove_listener(m_valueUpdateListener);
		}
	}

};

/**
 * End of chain
 */
template<class _Pt, class _PsT> class end_of_chain: public binding_link {
private:
	binding_chain<_Pt> &m_bindingChain;
public:
	explicit end_of_chain(binding_chain<_Pt> &_bindingChain) :
			m_bindingChain(_bindingChain) {
	}

	template<class _CsT> shared_ptr<end_of_chain<_Pt, _CsT>> bind(
			shared_ptr<binding_converter<_PsT, _CsT>> converter) {
		return m_bindingChain.bind(converter);
	}

	template<class _CsT> shared_ptr<binding_chain_controller> bind(
			shared_ptr<component_binding<_CsT>> _componentBinding) {
		return m_bindingChain.bind(_componentBinding).lock();
	}

	~end_of_chain() final = default;

};

}

#endif /* BINDING_CHAIN_HH_ */
