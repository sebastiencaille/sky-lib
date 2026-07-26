//! converters.rs
//!
//! Rust port of `converters.hh` + `converters.cpp` (originally
//! `IntConverters.hh` / `.cpp`).
//!
//! Original author: scaille (Mar 4, 2012)
//!
//! ## Design notes
//! The C++ body parses with `strtol` and rejects the input if: `errno` was
//! set (overflow/underflow), the string wasn't fully consumed (trailing
//! garbage), or the string was empty. `str::parse::<i32>()` covers the
//! overflow and full-consumption cases directly (it fails on any trailing
//! character and on out-of-range values) and empty strings fail to parse
//! too, so the explicit empty check is redundant but kept for parity and
//! a clearer error message. One deliberate divergence: `strtol` silently
//! skips *leading* whitespace before the number; this port does the same
//! via `trim_start()` (but, matching the original, does **not** allow
//! trailing whitespace — `*endPtr != '\0'` would already reject that).

use std::rc::Rc;

use crate::lib_properties::{ BindingConverter, GuiException, IntToString};

impl IntToString {
    pub fn new() -> Self {
        Self
    }

    pub fn of() -> Rc<dyn BindingConverter<i32, String>> {
        Rc::new(IntToString::new())
    }
}

impl Default for IntToString {
    fn default() -> Self {
        Self::new()
    }
}

impl BindingConverter<i32, String> for IntToString {
    fn convert_component_value_to_property_value(
        &self,
        component_value: &String,
    ) -> Result<i32, GuiException> {
        if component_value.is_empty() {
            return Err(GuiException::new(format!(
                "Invalid number: {component_value}"
            )));
        }
        component_value
            .trim_start()
            .parse::<i32>()
            .map_err(|_| GuiException::new(format!("Invalid number: {component_value}")))
    }

    fn convert_property_value_to_component_value(&self, property_value: &i32) -> String {
        property_value.to_string()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn round_trips_a_value() {
        let converter = IntToString::new();
        assert_eq!(converter.convert_property_value_to_component_value(&42), "42");
        assert_eq!(
            converter
                .convert_component_value_to_property_value(&"42".to_string())
                .unwrap(),
            42
        );
    }

    #[test]
    fn rejects_empty_string() {
        let converter = IntToString::new();
        assert!(converter
            .convert_component_value_to_property_value(&String::new())
            .is_err());
    }

    #[test]
    fn rejects_trailing_garbage() {
        let converter = IntToString::new();
        assert!(converter
            .convert_component_value_to_property_value(&"42abc".to_string())
            .is_err());
    }

    #[test]
    fn allows_leading_whitespace_like_strtol() {
        let converter = IntToString::new();
        assert_eq!(
            converter
                .convert_component_value_to_property_value(&"  42".to_string())
                .unwrap(),
            42
        );
    }

    #[test]
    fn rejects_overflow() {
        let converter = IntToString::new();
        assert!(converter
            .convert_component_value_to_property_value(&"99999999999".to_string())
            .is_err());
    }
}

