
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
#include <memory>

#include "types.hh"
#include "typed_property.hh"

namespace ch_skymarshall::gui {

using namespace std;
using namespace __gnu_cxx;

class error_notifier {

public:

	virtual void set_error(source_ptr _source, const gui_exception &_e) = 0;

	virtual void clear_error(source_ptr _source) = 0;

	virtual ~error_notifier() = default;
};

/**
 * Converters
 */

template<class _Pt, class _Ct> class binding_converter {
public:
	virtual _Pt convert_component_value_to_property_value(
			const _Ct& _componentValue) = 0;
	virtual _Ct convert_property_value_to_component_value(
			const _Pt& _propertyValue) = 0;

	virtual ~binding_converter() = default;

};

class gui_error_to_string: public binding_converter<gui_exception_ptr, string> {

public:
	gui_exception_ptr convert_component_value_to_property_value(
			const string& _componentValue) final {
		// nonsense
		return nullptr;
	}

	string convert_property_value_to_component_value(
			const gui_exception_ptr& _propertyValue) final {
		if (_propertyValue == nullptr) {
			return "";
		}
		return _propertyValue->what();
	}

	static shared_ptr<binding_converter<gui_exception_ptr, string>> of() {
		return make_shared<gui_error_to_string>();
	}
};

/** Link from component binding to chain */

template<class _CT> class component_link {
public:
	virtual void set_value_from_component(source_ptr _component,
			_CT _componentValue) = 0;
	virtual void reload_component_value() = 0;
protected:
	~component_link() = default;

};

/**
 * Definition of component bindings
 */
template<class _CT> class component_binding {

public:

	virtual void add_component_value_change_listener(
			weak_ptr<component_link<_CT>> _converter) = 0;

	virtual void remove_component_value_change_listener() = 0;

	virtual void set_component_value(property &_source, _CT _value) = 0;

	virtual source_ptr get_component() = 0;

	virtual ~component_binding() = default;
};

class binding_chain_dependency;

class binding_chain_controller {
public:
	virtual void attach() = 0;

	virtual void detach() = 0;

	virtual property& get_property() = 0;

	virtual shared_ptr<binding_chain_controller> add_dependency(
			shared_ptr<binding_chain_dependency> dependency) = 0;

	virtual ~binding_chain_controller() = default;
};

using namespace std::placeholders;

class binding_chain_dependency {
public:
	/* Registers the dependency. The dependency is already stored in the binding chain */
	virtual void register_dep(weak_ptr<binding_chain_controller> _chain,
			weak_ptr<binding_chain_dependency> _dependency) = 0;

	virtual ~binding_chain_dependency() = default;
};

/** Actions */

enum class property_group_actions {
	BEFORE_FIRE, AFTER_FIRE
};

class action {
public:
	virtual ~action() = default;

	virtual void apply(property_group_actions _action,
			const property *_property) = 0;

};

template<class _T> class action_dependency: public binding_chain_dependency {
private:

	weak_ptr<binding_chain_controller> m_chain;
	weak_ptr<property_listener_dispatcher> m_listener;

	std::function<
			void(property_group_actions _action, const property *_property)> m_action;

	void action_before(const source_ptr caller, property *_property) {
		m_action(property_group_actions::BEFORE_FIRE, _property);
	}

	void action_after(const source_ptr caller, property *_property) {
		m_action(property_group_actions::AFTER_FIRE, _property);
	}

public:

	explicit action_dependency(
			std::function<
					void(property_group_actions _action,
							const property *_property)> _action) :
			m_action(_action) {
	}

	void register_dep(weak_ptr<binding_chain_controller> _chain,
			weak_ptr<binding_chain_dependency> _myself) final {
		_chain.lock()->get_property().add_listener(
				property_listener_dispatcher::ofLazy(m_listener, _myself,
						std::bind(&action_dependency::action_before, this, _1,
								_2),
						std::bind(&action_dependency::action_after, this, _1,
								_2)));
	}

	void unbind() {
		m_chain.lock()->get_property().remove_listener(m_listener);
	}
};
}

#endif /* BINDINGINTERFACE_HH_ */
