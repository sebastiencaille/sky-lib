//! glib_converter.rs
//!
//! Rust port of `glib_converter.hh` + `glib_converter.cpp` (originally
//! `GlibConverter.hh`/`.cpp`).
//!
//! Original author: scaille (Apr 4, 2012)
//!
//! ## Design notes
//! `glibmm::ustring` is glibmm's UTF-8 string wrapper; the `gtk4-rs` /
//! `glib` crate's equivalent (the type GTK widget getters actually return,
//! e.g. `Entry::text()`) is [`glib::GString`]. This is a direct,
//! allocation-light conversion in both directions — no fallibility on
//! either side, matching the original (neither method threw).
//!
//! Requires the `glib` crate as a dependency (see the note in
//! `gtk_bindings.rs` for the full `Cargo.toml` this GTK layer needs).

use std::rc::Rc;

use glib::GString;

use crate::lib_properties::{BindingConverter, GuiException};

/// Port of `string_to_ustring`.
pub struct StringToUstring;

impl StringToUstring {
    pub fn new() -> Self {
        Self
    }

    pub fn of() -> Rc<dyn BindingConverter<String, GString>> {
        Rc::new(Self::new())
    }
}

impl Default for StringToUstring {
    fn default() -> Self {
        Self::new()
    }
}

impl BindingConverter<String, GString> for StringToUstring {
    fn convert_component_value_to_property_value(
        &self,
        component_value: &GString,
    ) -> Result<String, GuiException> {
        Ok(component_value.to_string())
    }

    fn convert_property_value_to_component_value(&self, property_value: &String) -> GString {
        GString::from(property_value.as_str())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn round_trips_a_value() {
        let converter = StringToUstring::new();
        let g = converter.convert_property_value_to_component_value(&"hello".to_string());
        assert_eq!(g.as_str(), "hello");
        assert_eq!(
            converter
                .convert_component_value_to_property_value(&g)
                .unwrap(),
            "hello"
        );
    }
}
