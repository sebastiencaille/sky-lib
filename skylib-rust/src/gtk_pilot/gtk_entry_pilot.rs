//! gtk_entry_pilot.rs
//!
//! Rust port of `gtk_entry_pilot.hh` + `.cpp` (originally
//! `GtkEntryPilot.h`/`.cpp`).
//!
//! Original author: scaille (Feb 26, 2021)

use std::cell::RefCell;
use std::rc::{Rc, Weak};
use std::time::Duration;

use gtk4::prelude::*;

use crate::lib_gui_pilot::abstract_gui_component::{AbstractGuiComponent, Polling};
use super::abstract_gtk_component::AbstractGtkComponentBase;
use super::gtk_gui_pilot::GtkGuiPilot;

/// Port of `gtk_entry_pilot`.
pub struct GtkEntryPilot {
    base: AbstractGtkComponentBase,
    entry: RefCell<Option<gtk4::Entry>>,
    myself: Weak<GtkEntryPilot>,
}

impl GtkEntryPilot {
    pub fn new(gui_pilot: Rc<GtkGuiPilot>, name: String) -> Rc<Self> {
        Rc::new_cyclic(|myself| Self {
            base: AbstractGtkComponentBase::new(gui_pilot, name),
            entry: RefCell::new(None),
            myself: myself.clone(),
        })
    }

    /// Port of `gtk_entry_pilot::set_text`.
    ///
    /// Note: like the original, the 30-second timeout failure path just
    /// aborts the operation loudly (the C++ `throw string("setText
    /// failed")`) — ported as a `panic!`, preserving the "this is a test
    /// harness, fail hard" behavior rather than introducing a `Result`
    /// return type the original didn't have.
    pub fn set_text(&self, text: String) {
        // Upgrading to an owned `Rc<Self>` and moving `text` by value (not
        // by reference) makes this closure genuinely `'static`, sidestepping
        // the borrow-lifetime issue `gtk_utils::get_run_in_gtk_scoped`
        // exists for — `AbstractGtkComponentBase::execute_polling` still
        // dispatches through the scoped helper underneath, which works
        // fine for `'static` closures too, just without needing to.
        let this = self.myself.upgrade().expect("pilot dropped while in use");
        let polling = Polling::new(move || {
            let mut entry_slot = this.entry.borrow_mut();
            if entry_slot.is_none() {
                if let Ok(widget) = this.base.gui_pilot.find_widget(&this.base.name) {
                    *entry_slot = widget.downcast::<gtk4::Entry>().ok();
                }
            }
            let Some(entry) = entry_slot.as_ref() else {
                return false;
            };
            if !AbstractGtkComponentBase::is_editable(entry) {
                return false;
            }
            entry.set_text(&text);
            true
        });

        self.base.wait(&polling, Duration::from_secs(30), |_p| {
            panic!("setText failed");
        });
    }
}
