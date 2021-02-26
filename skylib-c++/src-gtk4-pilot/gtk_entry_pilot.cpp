/*
 * GtkEntryPilot.cpp
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#include "gtk_entry_pilot.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace std;
using namespace ch_skymarshall::gui::pilot;

gtk_entry_pilot::gtk_entry_pilot(Gtk::Entry *_entry) :
		m_entry(_entry) {

}

void gtk_entry_pilot::set_text(string text) {
	polling set_text([&]() {
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
