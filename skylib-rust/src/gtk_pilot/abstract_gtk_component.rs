//! abstract_gtk_component.rs
//!
//! Rust port of `abstract_gtk_component.hh` + `.cpp` (originally
//! `AbstractGtkComponent.h`/`.cpp`).
//!
//! Original author: scaille (Feb 26, 2021)
//!
//! ## Design notes
//! - `gtk_gui_pilot* const m_gui_pilot` was a non-owning observer pointer.
//!   Rust has no equally free non-owning-reference-into-a-heap-object
//!   primitive without introducing lifetimes across these pilot types
//!   (which would infect every struct here), so this port uses a shared
//!   `Rc<GtkGuiPilot>` instead — a small ownership-model change, but safe
//!   and simple, and `GtkGuiPilot` doesn't hold pilots back so there's no
//!   cycle risk.
//! - `executePolling` dispatches the (possibly widget-touching) predicate
//!   onto the GTK main-loop thread via [`gtk_utils::get_run_in_gtk_scoped`]
//!   — see that function's doc comment for why the "scoped", non-`'static`
//!   variant is needed here specifically (the predicate closure built in
//!   `gtk_entry_pilot::set_text` borrows local state).

use gtk4::prelude::*;

use std::rc::Rc;

use crate::lib_gui_pilot::abstract_gui_component::{AbstractGuiComponent, Polling};
use crate::gtk::gtk_utils;
use super::gtk_gui_pilot::GtkGuiPilot;

/// Port of `abstract_gtk_component`'s shared fields — embedded by
/// composition in concrete pilot types (see `gtk_entry_pilot.rs`), the
/// way a C++ subclass would inherit them.
pub struct AbstractGtkComponentBase {
    pub gui_pilot: Rc<GtkGuiPilot>,
    pub name: String,
}

impl AbstractGtkComponentBase {
    pub fn new(gui_pilot: Rc<GtkGuiPilot>, name: impl Into<String>) -> Self {
        Self {
            gui_pilot,
            name: name.into(),
        }
    }

    /// Port of `isEditable`.
    pub fn is_editable(widget: &impl IsA<gtk4::Widget>) -> bool {
        widget.is_visible()
    }

    /// Port of `executePolling`, which ran the base class's
    /// `executePolling` body on the GTK thread via `get_run_in_gtk<bool>`.
    /// Here that base-class body is just "call the predicate" (see
    /// [`AbstractGuiComponent::execute_polling`]'s default), inlined
    /// directly since Rust has no `base_class::method()`-style explicit
    /// qualification to call a shadowed default trait method.
    fn dispatch_polling_to_gtk_thread(&self, polling: &Polling) -> bool {
        gtk_utils::get_run_in_gtk_scoped(|| (polling.polling_function())())
    }
}

impl AbstractGuiComponent for AbstractGtkComponentBase {
    fn execute_polling(&self, polling: &Polling) -> bool {
        self.dispatch_polling_to_gtk_thread(polling)
    }
}
