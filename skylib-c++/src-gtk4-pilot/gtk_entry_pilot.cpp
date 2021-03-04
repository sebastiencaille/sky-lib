/*
 * GtkEntryPilot.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include "gtk_entry_pilot.hh"
#include "gtk_gui_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace std;
using namespace ch_skymarshall::gui::pilot;

gtk_entry_pilot::gtk_entry_pilot(gtk_gui_pilot* _gui_pilot, string const& _name) :
		abstract_gtk_component(_gui_pilot, _name) {
}

void gtk_entry_pilot::set_text(string text) {
	polling set_text([&text, this]() {
		if (m_entry == NULL) {
			Gtk::Widget* found = m_gui_pilot->find_widget(m_name);
			m_entry = dynamic_cast<Gtk::Entry*>(found);
		}
		if (m_entry == NULL) {
			return false;
		}
		if (!isEditable(m_entry)) {
			return false;
		}
		m_entry->set_text(text);

		return true;
	});
	wait(set_text, std::chrono::seconds(30), [](polling &p) {
		throw string("setText failed");
	});
}

gtk_entry_pilot::~gtk_entry_pilot() {
	// TODO Auto-generated destructor stub
}

}
