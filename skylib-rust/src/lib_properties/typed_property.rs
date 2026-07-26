//! typed_property.rs
//!
//! Rust port of `typed_property.hh` (originally `Properties.hh`).
//!
//! Original author: scaille (Feb 19, 2012)
//!
//! ## Design notes
//! - `typed_property<_Pt>` inherits `property` and adds a value plus
//!   `get`/`set`/`force_changed`. As with `property.rs`, inheritance
//!   becomes composition: [`TypedProperty<T>`] embeds a
//!   [`crate::property::PropertyBase`] and implements the
//!   [`crate::property::Property`] trait by delegating to it.
//! - **Equality semantics differ from the original in one specific case.**
//!   `set()` guards with `if (m_value == _newValue) return;`. For most
//!   `_Pt` this is an ordinary value comparison, ported directly as
//!   `T: PartialEq`. But where `_Pt` is itself a `shared_ptr<X>` (as with
//!   `controller_property<gui_exception_ptr>`), C++'s
//!   `shared_ptr::operator==` compares **pointer identity**, not pointee
//!   value — while Rust's `Rc<X>: PartialEq` (via `#[derive(PartialEq)]`
//!   propagation) compares the **pointee value**. If `T = Rc<X>` and you
//!   need the original's identity-comparison behavior, compare with
//!   `Rc::ptr_eq` before calling `set`, rather than relying on the
//!   built-in check here.
//! - `typed_property_shared_ptr<_Pt>` was just `typed_property<shared_ptr<_Pt>>`
//!   with pass-through constructors; since Rust generics already let
//!   `TypedProperty<Rc<X>>` be written directly, [`TypedPropertySharedPtr`]
//!   is a plain type alias rather than a separate struct.

use std::cell::RefCell;
use std::rc::{Rc};

use crate::lib_properties::{Property, TypedProperty, PropertyBase, PropertyManager, SourcePtr};
use crate::lib_properties::property_helpers::PropertyHelper;

impl<T: PartialEq + Clone + 'static> PropertyHelper for TypedProperty<T> {
    fn get_base(&self) -> &PropertyBase {
        &self.base
    }
}

impl<T: PartialEq + Clone + 'static> Property for TypedProperty<T> {
    fn attach(&self) {
        PropertyHelper::get_base(self).attached.set(true);
        self.force_changed(Property::as_source_ptr(self))
    }
}

impl<T: PartialEq + Clone + 'static> TypedProperty<T> {

    /// Port of both C++ constructors (`const string_view&` / `const char*`
    /// name) — Rust's `&'static str` covers both call shapes.
    pub fn new(name: &'static str, manager: Rc<PropertyManager>, default_value: T) -> Self {
        Self {
            base: PropertyBase::new(name, manager),
            value: RefCell::new(default_value),
        }
    }

    pub fn get(&self) -> T {
        self.value.borrow().clone()
    }

    /// Port of `set(source_ptr, value_type const)`.
    pub fn set(&self, source: SourcePtr, new_value: T) {
        if *self.value.borrow() == new_value {
            return;
        }

        self.base.manager.fire_before_property_changed(source, self);

        let old_value = self.value.borrow().clone();
        *self.value.borrow_mut() = new_value;

        {
            // SAFETY-relevant note: these raw pointers are handed to
            // `fire_property_changed`, which only reads through them
            // synchronously while listeners run — mirroring the C++
            // `(const void*)&oldValue` / `(const void*)&_newValue`
            // pointers-to-locals pattern. They must not be retained past
            // this call.
            let current = self.value.borrow();
            let old_ptr = (&old_value as *const T).cast::<()>();
            let new_ptr = (&*current as *const T).cast::<()>();
            self.base
                .manager
                .fire_property_changed(source, self.base.name, old_ptr, new_ptr);
        }

        self.base.manager.fire_after_property_changed(source, self);
    }

    /// Port of `force_changed(source_ptr)`.
    pub fn force_changed(&self, source: SourcePtr) {
        let current = self.value.borrow();
        let new_ptr = (&*current as *const T).cast::<()>();
        self.base
            .manager
            .fire_property_changed(source, self.base.name, std::ptr::null(), new_ptr);
    }

}


#[cfg(test)]
mod tests {
    use super::*;
    use crate::lib_properties::PropertyListener;
    use std::cell::RefCell as StdRefCell;
    use glib::clone::Downgrade;

    struct RecordingListener {
        fired: StdRefCell<Vec<(String, bool, bool)>>,
    }

    impl PropertyListener for RecordingListener {
        fn fire(&self, _source: SourcePtr, name: &str, old: *const (), new: *const ()) {
            self.fired
                .borrow_mut()
                .push((name.to_string(), old.is_null(), new.is_null()));
        }
        fn before_change(&self, _source: SourcePtr, _property: &dyn Property) {}
        fn after_change(&self, _source: SourcePtr, _property: &dyn Property) {}
    }

    #[test]
    fn set_skips_when_value_unchanged() {
        let manager = Rc::new(PropertyManager::new());
        let listener = Rc::new(RecordingListener {
            fired: StdRefCell::new(Vec::new()),
        });
        manager.add_listener("count", listener.clone());

        let prop = TypedProperty::new("count", manager, 5i32);
        prop.set(std::ptr::null(), 5);
        assert!(listener.fired.borrow().is_empty());

        prop.set(std::ptr::null(), 6);
        assert_eq!(listener.fired.borrow().len(), 1);
        assert_eq!(prop.get(), 6);
    }

    #[test]
    fn force_changed_reports_null_old_value() {
        let manager = Rc::new(PropertyManager::new());
        let listener = Rc::new(RecordingListener {
            fired: StdRefCell::new(Vec::new()),
        });
        manager.add_listener("count", listener.clone());

        let prop = TypedProperty::new("count", manager, 1);
        prop.attach();

        prop.manager().remove_listener("count", listener.clone());

        let fired = listener.fired.borrow();

        assert_eq!(fired.len(), 1);
        assert!(fired[0].1, "old_value pointer should be null");
        assert!(!fired[0].2, "new_value pointer should not be null");
    }
}
