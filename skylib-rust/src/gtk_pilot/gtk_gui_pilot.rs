//! gtk_gui_pilot.rs
//!
//! Rust port of `gtk_gui_pilot.hh` + `.cpp` (originally `gtk_pilot.h`/`.cpp`).
//!
//! Original author: scaille (Feb 26, 2021)

use std::rc::Rc;

use gtk4::prelude::*;

use super::gtk_entry_pilot::GtkEntryPilot;
use crate::lib_properties::GuiException;

/// Port of `gui_pilot_exception`. The original added nothing over its base
/// beyond a `DESTR_LOG`'d destructor, so this is just a thin constructor
/// wrapping [`GuiException`] rather than a distinct type.
pub fn gui_pilot_exception(message: impl Into<String>) -> GuiException {
    GuiException::new(message)
}

/// Port of `gtk_gui_pilot`.
pub struct GtkGuiPilot {
    window: gtk4::Window,
}

impl GtkGuiPilot {
    pub fn new(window: gtk4::Window) -> Rc<Self> {
        Rc::new(Self { window })
    }

    /// Port of `gtk_gui_pilot::entry`.
    pub fn entry(self: &Rc<Self>, name: impl Into<String>) -> Rc<GtkEntryPilot> {
        GtkEntryPilot::new(Rc::clone(self), name.into())
    }

    /// Port of `gtk_gui_pilot::find_widget`.
    pub fn find_widget(&self, name: &str) -> Result<gtk4::Widget, GuiException> {
        let child = self
            .window
            .child()
            .ok_or_else(|| gui_pilot_exception(format!("No widget found: {name}")))?;
        Self::find(&child, name).ok_or_else(|| gui_pilot_exception(format!("No widget found: {name}")))
    }

    /// Port of the private recursive `gtk_gui_pilot::find`.
    fn find(widget: &gtk4::Widget, name: &str) -> Option<gtk4::Widget> {
        if widget.widget_name().as_str() == name {
            return Some(widget.clone());
        }
        let mut child = widget.first_child();
        while let Some(c) = child {
            if let Some(found) = Self::find(&c, name) {
                return Some(found);
            }
            child = c.next_sibling();
        }
        None
    }
}
