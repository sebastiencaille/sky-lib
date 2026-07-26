//! property.rs
//!
//! Rust port of `property.hh` + `property.cpp` (originally `Properties.hh`).
//!
//! Original author: scaille (Feb 19, 2012)
//!
//! ## Design notes
//!
//! C++'s `property` is an **abstract base class**: it owns the common
//! bookkeeping (name, owning `property_manager`, an `m_attached` flag) and
//! declares `attach()` pure virtual for subclasses to implement. Rust has
//! no inheritance, so this is split into two pieces, the way the rest of
//! this port handles the base-class-with-shared-state pattern:
//!
//! - [`Property`] — a trait capturing the abstract interface that other
//!   modules (`property_listener`, `property_manager`, `binding_chain`,
//!   ...) actually depend on.
//! - [`PropertyBase`] — a plain struct holding the fields `property.hh`
//!   declared (`m_name`, `m_manager`, `m_attached`), meant to be embedded
//!   by composition in concrete property types (see `typed_property.rs`),
//!   the way a C++ subclass would inherit them. Its `Drop` impl ports the
//!   `~property()` destructor, which deregisters all of this property's
//!   listeners from the manager.

use std::cell::Cell;
use std::rc::{Rc};
use crate::lib_properties::{PropertyManager, PropertyBase};


impl PropertyBase {
    pub fn new(name: &'static str, manager: Rc<PropertyManager>) -> Self {
        Self {
            name,
            manager,
            attached: Cell::new(false),
        }
    }
}

impl Drop for PropertyBase {
    fn drop(&mut self) {
        self.manager.remove_listeners(self.name);
    }
}
