//! gtk_bindings.rs
//!
//! Rust port of `gtk_bindings.hh` + `gtk_bindings.cpp` (originally
//! `GtkBindings.h`/`.cpp`).
//!
//! Original author: scaille (Apr 4, 2012)
//!
//! ## Design notes
//! `entry_binding`/`label_binding` are `ComponentBinding<GString>`
//! implementors that need to hand a *weak self-reference* to a GTK signal
//! handler closure (`sigc::mem_fun(*this, ...)` in the original). As with
//! `ActionDependency` and `BindingChain` earlier in this port, that's done
//! via `Rc::new_cyclic` rather than a raw `this` pointer.
//!
//! This file needs, in `Cargo.toml`:
//! ```toml
//! [dependencies]
//! gtk = { package = "gtk4", version = "0.7" }
//! glib = "0.18"
//! ```
//! (Pin these — or whatever recent, mutually-compatible pair your
//! toolchain resolves — to versions your `rustc` can actually build; very
//! new `gtk4-rs`/`glib` releases pull in transitive deps that need a
//! 2024-edition-aware Cargo.)

use std::cell::RefCell;
use std::rc::{Rc, Weak};

use glib::GString;
use gtk4::prelude::*;

use crate::lib_properties::{ComponentBinding, ComponentLink, Property, SourcePtr};

/// Port of `entry_binding`.
pub struct EntryBinding {
    entry: gtk4::Entry,
    myself: Weak<EntryBinding>,
    component_link: RefCell<Option<Weak<dyn ComponentLink<GString>>>>,
    connection: RefCell<Option<glib::SignalHandlerId>>,
}

impl EntryBinding {
    pub fn new(entry: gtk4::Entry) -> Rc<Self> {
        Rc::new_cyclic(|myself| Self {
            entry,
            myself: myself.clone(),
            component_link: RefCell::new(None),
            connection: RefCell::new(None),
        })
    }

    /// Port of `entry_binding::of`.
    pub fn of(entry: gtk4::Entry) -> Rc<dyn ComponentBinding<GString>> {
        EntryBinding::new(entry)
    }

    /// Port of `entry_binding::on_changed_signal`.
    fn on_changed_signal(&self) {
        let text = self.entry.text();
        if let Some(link) = self
            .component_link
            .borrow()
            .as_ref()
            .and_then(Weak::upgrade)
        {
            link.set_value_from_component(self.get_component(), text);
        }
    }
}

impl ComponentBinding<GString> for EntryBinding {
    fn add_component_value_change_listener(&self, listener: Weak<dyn ComponentLink<GString>>) {
        if self.connection.borrow().is_none() {
            *self.component_link.borrow_mut() = Some(listener);
            let weak_self = self.myself.clone();
            let handler = self.entry.connect_changed(move |_entry| {
                if let Some(this) = weak_self.upgrade() {
                    this.on_changed_signal();
                }
            });
            *self.connection.borrow_mut() = Some(handler);
        }
    }

    fn remove_component_value_change_listener(&self) {
        if let Some(handler) = self.connection.borrow_mut().take() {
            self.entry.disconnect(handler);
        }
    }

    fn set_component_value(&self, _source: &dyn Property, value: GString) {
        if self.entry.text() != value {
            self.entry.set_text(&value);
        }
    }

    fn get_component(&self) -> SourcePtr {
        self.entry.as_ptr() as *const ()
    }
}

impl Drop for EntryBinding {
    /// Port of `~entry_binding()`, which calls
    /// `remove_component_value_change_listener()`.
    fn drop(&mut self) {
        self.remove_component_value_change_listener();
    }
}

/// Port of `label_binding` — read-only, so the listener-registration
/// methods are no-ops, matching the original's empty bodies.
pub struct LabelBinding {
    label: gtk4::Label,
}

impl LabelBinding {
    pub fn new(label: gtk4::Label) -> Rc<Self> {
        Rc::new(Self { label })
    }

    /// Port of `label_binding::of`.
    pub fn of(label: gtk4::Label) -> Rc<dyn ComponentBinding<GString>> {
        LabelBinding::new(label)
    }
}

impl ComponentBinding<GString> for LabelBinding {
    fn add_component_value_change_listener(&self, _listener: Weak<dyn ComponentLink<GString>>) {
        // label is read-only
    }

    fn remove_component_value_change_listener(&self) {
        // label is read-only
    }

    fn set_component_value(&self, _source: &dyn Property, value: GString) {
        self.label.set_text(&value);
    }

    fn get_component(&self) -> SourcePtr {
        self.label.as_ptr() as *const ()
    }
}
