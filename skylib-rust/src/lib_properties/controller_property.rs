//! controller_property.rs
//!
//! Rust port of `controller_property.hh` (originally `ControllerProperty.hh`).
//!
//! Original author: scaille (Mar 4, 2012)
//!
//! ## Design notes
//! The C++ `bind()` overloads build a closure via
//! `std::bind(&controller_property::set, this, _1, _2)`, capturing a raw
//! `this`. That's safe there only because the property is assumed to
//! outlive any binding chain built from it. To make the equivalent safe
//! in Rust without relying on that assumption, `ControllerProperty<Pt>`
//! is always constructed behind an `Rc` (`new` returns `Rc<Self>`), and
//! its `bind_*` methods take `self: &Rc<Self>` so they can capture a
//! strong `Rc<Self>` in the setter closure instead of a raw pointer.

use std::rc::Rc;

use crate::lib_properties::{TypedProperty, PropertyManager, SourcePtr, BindingConverter, ErrorNotifier, ControllerProperty, BindingChainController, ComponentBinding};
use super::binding_chain::{BindingChain, EndOfChain};


impl<Pt: PartialEq + Clone + 'static> ControllerProperty<Pt> {
    pub fn new(
        name: &'static str,
        manager: Rc<PropertyManager>,
        default_value: Pt,
        error_notifier: Option<Rc<dyn ErrorNotifier>>,
    ) -> Rc<Self> {
        Rc::new(Self {
            property: Rc::new(TypedProperty::new(name, manager, default_value)),
            error_notifier,
        })
    }

    pub fn get(&self) -> Pt {
        self.property.get()
    }

    pub fn set(&self, source: SourcePtr, value: Pt) {
        self.property.set(source, value);
    }

    fn setter(self: &Rc<Self>) -> Rc<dyn Fn(SourcePtr, Pt)> {
        let this = Rc::clone(self);
        Rc::new(move |source, value| this.set(source, value))
    }

    /// Port of the `bind(shared_ptr<binding_converter<_Pt, _Cst>>)` overload.
    pub fn bind_converter<Cst: 'static>(
        self: &Rc<Self>,
        converter: Rc<dyn BindingConverter<Pt, Cst>>,
    ) -> Rc<EndOfChain<Pt, Cst>> {
        let chain = BindingChain::of(self.property.clone(), self.error_notifier.clone());
        let end = chain.bind_property(self.setter());
        end.bind_converter(converter)
    }

    /// Port of the `bind(shared_ptr<component_binding<_Cst>>)` overload.
    pub fn bind_component<Cst: 'static>(
        self: &Rc<Self>,
        component_binding: Rc<dyn ComponentBinding<Cst>>,
    ) -> Rc<dyn BindingChainController> {
        let chain = BindingChain::of(self.property.clone(), self.error_notifier.clone());
        let end = chain.bind_property(self.setter());
        end.bind_component(component_binding)
    }
}

