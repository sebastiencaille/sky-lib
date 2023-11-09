
#include "property.hh"
#include "utils.hh"

namespace ch_skymarshall::gui {

using namespace std;

property::property(const string_view &_name, property_manager &_manager) :
		m_name(_name), m_manager(_manager) {
}

property::~property() {
	DESTR_LOG("~property " << m_name);
	m_manager.remove_listeners(m_name);
}

void property::attach() {
	m_attached = true;
}

const string_view& property::name() const {
	return m_name;
}

void property::add_listener(shared_ptr<property_listener> _listener) {
	m_manager.add_listener(m_name, _listener);
}

void property::remove_listener(weak_ptr<property_listener> _listener) {
	m_manager.remove_listener(m_name, _listener);
}

}
