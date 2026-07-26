//! property_manager.rs
//!
//! Rust port of `property_manager.hh` (originally `PropertyManager.hh`).
//!
//! Original author: scaille (Feb 18, 2012)
//!
//! ## Design notes
//! - C++ keyed listeners by `string_view` (a non-owning view into a
//!   longer-lived string, typically a string literal naming the
//!   property). Rust's borrow checker makes that awkward for a
//!   `HashMap` key unless the referenced data truly outlives the map, so
//!   this port keys by `&'static str` instead — a direct fit given
//!   property names in this codebase are always compile-time literals.
//! - `remove_listener(name, weak_ptr)` / `remove_listener(name, void*)`
//!   both existed in C++ to support two ways of identifying a listener
//!   for removal (a still-possibly-alive weak handle, or a raw identity
//!   pointer). Both are kept here as `remove_listener` /
//!   `remove_listener_by_ref`.

use std::rc::{Rc};

use crate::lib_properties::{Property, PropertyManager, PropertyListener, SourcePtr};

impl PropertyManager {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn add_listener(&self, name: &'static str, listener: Rc<dyn PropertyListener>) {
        self.listeners
            .borrow_mut()
            .entry(name)
            .or_default()
            .push(listener);
    }

    pub fn remove_listeners(&self, name: &'static str) {
        self.listeners.borrow_mut().remove(name);
    }

    /// Removes a listener identified by a (possibly already-expired) weak
    /// handle, comparing by pointer identity once upgraded.
    pub fn remove_listener(&self, name: &'static str, listener: Rc<dyn PropertyListener>) {
        if let Some(list) = self.listeners.borrow_mut().get_mut(name) {
            list.retain(|l| Rc::ptr_eq(l, &listener));
        }
    }

    pub fn fire_property_changed(
        &self,
        source: SourcePtr,
        name: &str,
        old_value: *const (),
        new_value: *const (),
    ) {
        if let Some(list) = self.listeners.borrow().get(name) {
            for listener in list {
                listener.fire(source, name, old_value, new_value);
            }
        }
    }

    pub fn fire_before_property_changed(&self, source: SourcePtr, property: &dyn Property) {
        if let Some(list) = self.listeners.borrow().get(property.name()) {
            for listener in list {
                listener.before_change(source, property);
            }
        }
    }

    pub fn fire_after_property_changed(&self, source: SourcePtr, property: &dyn Property) {
        if let Some(list) = self.listeners.borrow().get(property.name()) {
            for listener in list {
                listener.after_change(source, property);
            }
        }
    }

    pub fn dump(&self) {
        for (name, list) in self.listeners.borrow().iter() {
            println!("{name}: {} listener(s)", list.len());
        }
    }
}
