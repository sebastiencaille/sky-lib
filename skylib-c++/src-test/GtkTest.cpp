/*
 * GtkTest.cpp
 *
 *  Created on: Apr 4, 2012
 *      Author: scaille
 */
#include <vector>
#include <gtkmm.h>
#include <iostream>
#include <memory>
#include <thread>

#include <pangomm/attrlist.h>

#define DEBUG_DESTR

#include <controller_property.hh>
#include <binding_interface.hh>
#include <input_error_property_impl.hh>
#include <converters.hh>

#include <list_model.hh>
#include <glib_converter.hh>
#include <gtk_bindings.hh>
#include <gtk_gui_pilot.hh>

using namespace ch_skymarshall::gui;
using namespace ch_skymarshall::gui::converters;
using namespace ch_skymarshall::gui::glib;
using namespace ch_skymarshall::gui::gtk4;
using namespace ch_skymarshall::gui::gtk4::pilot;

using namespace std;
using namespace __gnu_cxx;

class GtkTest: public Gtk::Window {

private:

	class TestStringPropertyListener {

	public:
		explicit TestStringPropertyListener() = default;

		void propertyChanged(source_ptr _source, const string_view &_name,
				const void *_oldValue, const void *_newValue) const {
			cout << " TestStringPropertyListener fired: "
					<< *(const string*) _oldValue << " -> "
					<< *(const string*) _newValue << endl;
		}

	};

	class dep_test: public binding_chain_dependency {

		TestStringPropertyListener m_testListener;
		weak_ptr<property_listener_dispatcher> m_listener;
		weak_ptr<binding_chain_controller> m_controller;

	public:
		dep_test() = default;
		~dep_test() override
		DESTR_WITH_LOG("~dep_test")

		void register_dep(weak_ptr<binding_chain_controller> _controller,
				weak_ptr<binding_chain_dependency> _myself) final {
			m_controller = _controller;
			auto listener = property_listener_dispatcher::ofLazy(m_listener,
					_myself,
					sigc::mem_fun(this->m_testListener,
							&TestStringPropertyListener::propertyChanged));
			m_controller.lock()->get_property().add_listener(listener);
		}

		void unbind() const {
			if (auto lock = m_listener.lock()) {
				m_controller.lock()->get_property().remove_listener(m_listener);
			}
		}

		static shared_ptr<binding_chain_dependency> of() {
			return make_shared<dep_test>();
		}

	};
public:
	GtkTest();

	~GtkTest() override;

	void testGui();

	void apply_action(property_group_actions _action,
			const property *_property) const;

private:

	//Member widgets:
	Gtk::Entry m_entry;
	Gtk::Label m_label;
	Gtk::Entry m_intEntry;
	Gtk::Label m_intLabel;
	Gtk::Label m_error;
	Gtk::Box m_box;

	property_manager m_manager;

	shared_ptr<input_error_property> m_errorProperty =
			make_shared<input_error_property>("Errors", m_manager);

	controller_property<string> testProperty1 = controller_property<string>(
			"TestProp1", m_manager, "", m_errorProperty);

	controller_property<int> testProperty2 = controller_property<int>(
			"TestProp2", m_manager, 0, m_errorProperty);

};

GtkTest::GtkTest() :
		m_box(Gtk::Orientation::VERTICAL) {

	// *********** Panel ***********
	set_title("Basic application");
	set_child(m_box);
	m_box.set_margin(10);

	// *********** Basic text -> label ***********

	testProperty1.bind(string_to_ustring::of())->bind(
			entry_binding::of(m_entry))->add_dependency(dep_test::of());
	m_entry.set_expand();
	m_entry.set_name("Entry");
	m_box.append(m_entry);

	auto dep = make_shared<action_dependency<GtkTest>>(
			[this](property_group_actions group, const property *action) {
				this->apply_action(group, action);
			});

	testProperty1.bind(string_to_ustring::of())->bind(
			label_binding::of(m_label))->add_dependency(dep);
	m_label.set_expand();
	m_box.append(m_label);

	// *********** int text -> label ***********

	testProperty2.bind(int_to_string::of())->bind(string_to_ustring::of())->bind(
			entry_binding::of(m_intEntry));
	m_intEntry.set_expand();
	m_intEntry.set_name("IntEntry");
	m_box.append(m_intEntry);

	testProperty2.bind(int_to_string::of())->bind(string_to_ustring::of())->bind(
			label_binding::of(m_intLabel));
	m_intLabel.set_expand();
	m_box.append(m_intLabel);

	// *********** error ***********

	m_errorProperty->bind(gui_error_to_string::of())->bind(
			string_to_ustring::of())->bind(label_binding::of(m_error));

	m_error.set_expand();
	m_box.append(m_error);

	Pango::Attribute redTextFormatting = Pango::Attribute::create_attr_foreground(0xefef,
			0x2929, 0x2929);
	Pango::AttrList atrlist;
	atrlist.insert(redTextFormatting);
	m_error.set_attributes(atrlist);

	// Run

	testProperty2.set(NULL, 1);

	// Run the test
	thread testGui(&GtkTest::testGui, this);
	testGui.detach();
}

/**
 * Test gui by changing some values
 */

void GtkTest::testGui() {
	gtk_gui_pilot guiPilot(this);
	shared_ptr<gtk_entry_pilot> entryPilot = guiPilot.entry("Entry");
	shared_ptr<gtk_entry_pilot> intEntryPilot = guiPilot.entry("IntEntry");

	entryPilot->set_text("Hello");
	intEntryPilot->set_text("123");

}



GtkTest::~GtkTest()
DESTR_WITH_LOG("~GtkTest" << endl)

void GtkTest::apply_action(property_group_actions _action,
		const property *_property) const {
	switch (_action) {
	case property_group_actions::BEFORE_FIRE:
		cout << "BEFORE: " << _property->name() << endl;
		break;
	case property_group_actions::AFTER_FIRE:
		cout << "AFTER: " << _property->name() << endl;
		break;
	default:
		break;
	}
}

using int_model = list_model<int>;

void testList() {
	int_model int_list(int_model::sorted([](const int i1, const int i2) {
		return i1 - i2;
	}));
	int_list.insert(2);
	int_list.insert(1);
	cout << int_list.get_element_at(0) << " " << int_list.get_element_at(1)
			<< std::endl;
}

int main(int argc, char *argv[]) {

	testList();

	auto app = Gtk::Application::create("ch.skymarshall.example");
	return app->make_window_and_run < GtkTest > (argc, argv);
}

