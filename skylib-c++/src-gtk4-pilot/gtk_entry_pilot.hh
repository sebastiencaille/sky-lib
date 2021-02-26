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
	Gtk::Entry *m_entry;

public:
	gtk_entry_pilot(Gtk::Entry *_entry);
	virtual ~gtk_entry_pilot();

	void set_text(string text);
};
}
#endif /* GTKENTRYPILOT_H_ */
