//! gtk_test.rs
//!
//! Rust port of `GtkTest.cpp` — an example application wiring the whole
//! property/binding stack (`ControllerProperty`, converters, GTK
//! component bindings, dependencies) up to a real `gtk4` window, plus a
//! background "pilot" thread that drives the UI the way an integration
//! test would.
//!
//! Original author: scaille (Apr 4, 2012)
//!
//! ## Design notes
//! - C++ builds `GtkTest` as a `gtk4::Window` subclass with property/error
//!   fields as members, all initialized in the constructor's init list.
//!   Rust has no inheritance, so `GtkTest` here *wraps* a `gtk4::Window`
//!   (via `gtk4::ApplicationWindow`) plus holds the same fields by
//!   composition, and widget setup that the constructor performed now
//!   lives in `GtkTest::new`.
//! - `dep_test` used `sigc::mem_fun` on a plain (non-`shared_ptr`-managed)
//!   member object; here `TestStringPropertyListener` doesn't need its
//!   own type at all — it's inlined as a closure, which is the more
//!   idiomatic Rust shape for "a function bound to some captured state".
//! - The GTK signal (`gtk4::Application::run`) and the `thread`/`detach`
//!   pilot-test pattern are preserved via `std::thread::spawn` +
//!   `glib::idle_add_once` dispatch, matching how `gtk_utils` already
//!   bridges threads elsewhere in this port.

use std::rc::Rc;

use gtk4::prelude::*;
use gtk4::{glib, Application, ApplicationWindow};

mod utils;
mod lib_properties;
mod lib_gui_pilot;
mod gtk_pilot;
mod gtk;

use crate::lib_properties::{ControllerProperty, BindingChainDependency, ActionDependency, PropertyGroupActions, ErrorNotifier, GuiErrorToString, InputErrorProperty, Property, PropertyManager, IntToString };
use crate::gtk_pilot::gtk_gui_pilot::GtkGuiPilot;
use crate::gtk::{
    glib_converter::StringToUstring,
    gtk_bindings::{EntryBinding, LabelBinding},
    gtk_utils::AssertSend
};

const APP_ID: &str = "ch.skymarshall.example";

/// Port of `GtkTest::dep_test` — logs `before_change`/`after_change`
/// notifications for whatever property it's attached to. The original's
/// `TestStringPropertyListener` inner class is folded directly into the
/// closure passed to `ActionDependency::new`, since it held no state of
/// its own beyond the method being bound.
fn make_dep_test() -> Rc<dyn BindingChainDependency> {
    ActionDependency::<()>::new(Rc::new(
        |action: PropertyGroupActions, property: &dyn Property| match action {
            PropertyGroupActions::BeforeFire => {
                println!(" TestStringPropertyListener fired (before): {}", property.name());
            }
            PropertyGroupActions::AfterFire => {
                println!(" TestStringPropertyListener fired (after): {}", property.name());
            }
        },
    ))
}

/// Port of `GtkTest::apply_action`.
fn apply_action(action: PropertyGroupActions, property: &dyn Property) {
    match action {
        PropertyGroupActions::BeforeFire => println!("BEFORE: {}", property.name()),
        PropertyGroupActions::AfterFire => println!("AFTER: {}", property.name()),
    }
}

/// Port of the `GtkTest` window class.
pub struct GtkTest {
    pub window: ApplicationWindow,
    #[allow(dead_code)]
    manager: Rc<PropertyManager>,
    error_property: Rc<InputErrorProperty>,
    test_property1: Rc<ControllerProperty<String>>,
    test_property2: Rc<ControllerProperty<i32>>,
}

impl GtkTest {
    pub fn new(app: &Application) -> Rc<Self> {
        let manager = Rc::new(PropertyManager::new());
        let error_property = InputErrorProperty::new("Errors", manager.clone(), None, None);
        let error_notifier: Rc<dyn ErrorNotifier> = error_property.clone();

        let test_property1 = ControllerProperty::<String>::new(
            "TestProp1",
            manager.clone(),
            String::new(),
            Some(error_notifier.clone()),
        );
        let test_property2 = ControllerProperty::<i32>::new(
            "TestProp2",
            manager.clone(),
            0,
            Some(error_notifier),
        );

        let window = ApplicationWindow::builder()
            .application(app)
            .title("Basic application")
            .build();

        let this = Rc::new(Self {
            window,
            manager,
            error_property,
            test_property1,
            test_property2,
        });

        this.build_ui();
        this
    }

    fn build_ui(self: &Rc<Self>) {
        let box_ = gtk4::Box::new(gtk4::Orientation::Vertical, 0);
        box_.set_margin_top(10);
        box_.set_margin_bottom(10);
        box_.set_margin_start(10);
        box_.set_margin_end(10);
        self.window.set_child(Some(&box_));

        // *********** Basic text -> label ***********
        let entry = gtk4::Entry::new();
        entry.set_hexpand(true);
        entry.set_widget_name("Entry");

        self.test_property1
            .bind_converter(StringToUstring::of())
            .bind_component(EntryBinding::of(entry.clone()))
            .add_dependency(make_dep_test());
        box_.append(&entry);

        let label = gtk4::Label::new(None);
        label.set_hexpand(true);

        let action_dep = ActionDependency::<()>::new(Rc::new(apply_action));

        self.test_property1
            .bind_converter(StringToUstring::of())
            .bind_component(LabelBinding::of(label.clone()))
            .add_dependency(action_dep);
        box_.append(&label);

        // *********** int text -> label ***********
        let int_entry = gtk4::Entry::new();
        int_entry.set_hexpand(true);
        int_entry.set_widget_name("IntEntry");

        self.test_property2
            .bind_converter(IntToString::of())
            .bind_converter(StringToUstring::of())
            .bind_component(EntryBinding::of(int_entry.clone()));
        box_.append(&int_entry);

        let int_label = gtk4::Label::new(None);
        int_label.set_hexpand(true);

        self.test_property2
            .bind_converter(IntToString::of())
            .bind_converter(StringToUstring::of())
            .bind_component(LabelBinding::of(int_label.clone()));
        box_.append(&int_label);

        // *********** error ***********
        let error_label = gtk4::Label::new(None);
        error_label.set_hexpand(true);

        self.error_property
            .bind_converter(GuiErrorToString::of())
            .bind_converter(StringToUstring::of())
            .bind_component(LabelBinding::of(error_label.clone()));
        box_.append(&error_label);

        // Original used Pango::Attribute::create_attr_foreground for red
        // error text; gtk4-rs's CSS-provider route is the idiomatic
        // equivalent for static styling like this.
        let css = gtk4::CssProvider::new();
        css.load_from_data("label.error-text { color: #ef2929; }");
        error_label.add_css_class("error-text");
        gtk4::style_context_add_provider_for_display(
            &WidgetExt::display(&error_label),
            &css,
            gtk4::STYLE_PROVIDER_PRIORITY_APPLICATION,
        );

        // Run
        self.test_property2.set(std::ptr::null(), 1);

        // Run the pilot test, mirroring `thread testGui(...); testGui.detach();`.
        // `gtk4::Window` isn't `Send`, but nothing in `test_gui` actually
        // touches it except via `GtkGuiPilot`/`GtkEntryPilot`, which route
        // every widget access back through `get_run_in_gtk_scoped` onto
        // the main thread — see `gtk_utils::AssertSend`'s doc comment.
        let window_for_pilot = AssertSend::new(self.window.clone().upcast::<gtk4::Window>());
        std::thread::spawn(move || {
            GtkTest::test_gui(window_for_pilot.into_inner());
        });
    }

    /// Port of `GtkTest::testGui`.
    ///
    /// Runs on a background thread; `GtkGuiPilot`/`GtkEntryPilot` dispatch
    /// their widget-touching work back onto the GTK main-loop thread
    /// internally (see `gtk_utils::get_run_in_gtk_scoped`), so it's safe
    /// to drive the UI from here.
    fn test_gui(window: gtk4::Window) {
        let gui_pilot = GtkGuiPilot::new(window);
        let entry_pilot = gui_pilot.entry("Entry");
        let int_entry_pilot = gui_pilot.entry("IntEntry");

        entry_pilot.set_text("Hello".to_string());
        int_entry_pilot.set_text("123".to_string());
    }
}

/// Port of `main()`.
pub fn main() -> glib::ExitCode {
    let app = Application::builder().application_id(APP_ID).build();
    app.connect_activate(|app| {
        let test = GtkTest::new(app);
        test.window.present();
    });
    app.run()
}

