/*
 * GtkEntryPilot.h
 *
 *  Created on: Feb 26, 2021
 *      Author: scaille
 */

#ifndef GTKENTRYPILOT_H_
#define GTKENTRYPILOT_H_

#include <memory>
#include <gtkmm.h>
#include <string>

#include "abstract_gtk_component.hh"

namespace ch_skymarshall::gui::gtk4::pilot {

using namespace std;

class gtk_entry_pilot: public abstract_gtk_component {
private:
	Gtk::Entry *m_entry = nullptr;

public:
	gtk_entry_pilot(gtk_gui_pilot* _gui_pilot, const string & _name);
	~gtk_entry_pilot() override;

	void set_text(string text);
};
}
#endif /* GTKENTRYPILOT_H_ */
