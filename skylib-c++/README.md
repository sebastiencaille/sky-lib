# MVC POC (C++)

Concepts  [[here](../skylib-java)] 

The POC is based on GTK4. The compilation requires gtkmm4.
To compile, use make all in Debug folder

The main file is [[GtkTest.cpp](src-test/GtkTest.cpp)]

**Basic Examples**  

```
using namespace ch_scaille::gui;
using namespace ch_scaille::gui::converters;
using namespace ch_scaille::gui::glib;
using namespace ch_scaille::gui::gtk4;
...

// View
Gtk::Entry m_entry;
Gtk::Label m_label;

// Model
property_manager m_manager;
shared_ptr<input_error_property> m_errorProperty = make_shared<_error_property>("Errors", m_manager);
controller_property<string> testProperty1 = controller_property<string>("TestProp1", m_manager, "", m_errorProperty);

...
// Bindings
testProperty1.bind(string_to_ustring::of())->bind(entry_binding::of(m_entry));
testProperty1.bind(string_to_ustring::of())->bind(label_binding::of(m_label));
```

**Gtk gui pilot**

It is also possible to pilot the Gtk gui the following way.

```
using namespace ch_scaille::gui::gtk4::pilot;
...

gtk_gui_pilot guiPilot(this);
shared_ptr<gtk_entry_pilot> entryPilot = guiPilot.entry("Entry");
shared_ptr<gtk_entry_pilot> intEntryPilot = guiPilot.entry("IntEntry");

entryPilot->set_text("Hello");
```
	