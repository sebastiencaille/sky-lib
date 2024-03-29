

#include <iostream>
#include <memory>

#include "property_manager.hh"
#include "property.hh"
#include "utils.hh"


namespace ch_skymarshall::gui {

using namespace std;

property_manager::property_manager() = default;

property_manager::~property_manager() DESTR_WITH_LOG("~property_manager")

void property_manager::add_listener(const string_view& _name, shared_ptr<property_listener> _listener) {
	auto iter = m_propertyListeners.find(_name);
	shared_ptr<listener_list_type> plist;
	if (iter == m_propertyListeners.end()) {
		plist = make_shared<listener_list_type>();
		m_propertyListeners[_name] = plist;
	} else {
		plist = (*iter).second;
	}
	plist->push_back(_listener);
}

void property_manager::remove_listener(const string_view& _name, weak_ptr<property_listener> _listener) {
	auto iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	auto plist = (*iter).second;
	plist->remove(_listener.lock());
	if (plist->empty()) {
		m_propertyListeners.erase(_name);
	}
}

void property_manager::remove_listeners(const string_view &_name) {
	m_propertyListeners.erase(_name);
}

void property_manager::fire_property_changed(source_ptr _source, const string_view &_name, const void* _oldValue,
		const void* _newValue) const {
	auto iter = m_propertyListeners.find(_name);
	if (iter == m_propertyListeners.end()) {
		return;
	}
	auto plist((*iter).second);
	for (auto listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->fire(_source, _name, _oldValue, _newValue);
	}
}

void property_manager::fire_before_property_changed(source_ptr _source, property* _property) const {
	auto iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	auto plist((*iter).second);
	for (auto listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->before_change(_source, _property);
	}
}

void property_manager::fire_after_property_changed(source_ptr _source, property* _property) const {
	auto iter = m_propertyListeners.find(_property->name());
	if (iter == m_propertyListeners.end()) {
		return;
	}
	auto plist((*iter).second);
	for (auto listener = plist->begin(); listener != plist->end(); listener++) {
		(*listener)->after_change(_source, _property);
	}
}

void property_manager::dump() const {
	for (auto& mapListener: m_propertyListeners) {
		auto plist = mapListener.second;
		for (auto listener = plist->begin(); listener != plist->end(); listener++) {
			cout << "  " << hex << *listener << endl;
		}
	}

}

}
