//! input_error_property_impl.rs
//!
//! Rust port of `input_error_property_impl.hh` (originally
//! `ErrorPropertyImpl.hh`).
//!
//! Original author: scaille (Feb 28, 2012)
//!
//! ## Design notes
//! - `controller_property<gui_exception_ptr>` used `nullptr` as both the
//!   default value and the "no error" sentinel — a `shared_ptr` being
//!   implicitly nullable. `Rc<GuiException>` can't be null, so (as in
//!   `binding_interface.rs`'s `GuiErrorToString`) this instantiates
//!   `ControllerProperty<Option<GuiExceptionPtr>>` instead.
//! - C++ multiply-inherits `error_notifier` and
//!   `controller_property<gui_exception_ptr>`. Rust doesn't have multiple
//!   inheritance, so `InputErrorProperty` holds a `ControllerProperty` by
//!   composition and implements `ErrorNotifier` directly (delegating to
//!   the inner property), while also implementing `Property` itself by
//!   delegating to the same inner property — reproducing both C++ base
//!   classes' public interfaces on one Rust type.
//! - `clear_error` compared `_source == static_cast<property*>(this)` —
//!   ported using [`Property::as_source_ptr`].

use std::rc::Rc;

use crate::lib_properties::{ErrorNotifier, Property,  GuiException, SourcePtr, InputErrorProperty};

impl ErrorNotifier for InputErrorProperty {
    fn set_error(&self, source: SourcePtr, error: &GuiException) {
        self.property.set(source, Some(Rc::new(error.clone())));
    }

    fn clear_error(&self, source: SourcePtr) {
        if source == self.property.as_source_ptr() {
            return;
        }
        self.property.set(source, None);
    }
}
