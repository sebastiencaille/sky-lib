use std::any::Any;
use std::cell::{Cell, RefCell};
use std::collections::HashMap;
use std::rc::{Rc, Weak};
use crate::lib_properties::property_helpers::PropertyHelper;
use crate::lib_properties::property_listener::PropertyListenerDispatcher;

pub mod types;
pub mod property_listener;
pub mod property_manager;
pub mod property;
pub mod typed_property;
pub mod binding_interface;
pub mod binding_chain;
pub mod controller_property;
pub mod converters;
pub mod input_error_property_impl;

pub type SourcePtr = *const ();

struct PropertyBase {
    name: &'static str,
    manager: Rc<PropertyManager>,
    attached: Cell<bool>,
}

mod property_helpers {
    use super::PropertyBase;

    pub trait PropertyHelper {
        fn get_base(&self) -> &PropertyBase;
    }

}

pub trait Property: PropertyHelper {
    fn name(&self) -> &'static str {
        PropertyHelper::get_base(self).name
    }

    fn manager(&self) -> &PropertyManager {
        PropertyHelper::get_base(self).manager.as_ref()
    }

    fn attach(&self) {
        PropertyHelper::get_base(self).attached.set(true);
    }

    fn add_listener(&self, listener: Rc<dyn PropertyListener>) {
        PropertyHelper::get_base(self).manager.add_listener(PropertyHelper::get_base(self).name, listener);
    }

    fn remove_listener(&self, listener: Rc<dyn PropertyListener>) {
        PropertyHelper::get_base(self).manager.remove_listener(PropertyHelper::get_base(self).name, listener);
    }

    fn as_source_ptr(&self) -> SourcePtr {
        (self as *const Self as *const ()).cast()
    }
}

pub struct TypedProperty<T> {
    base: PropertyBase,
    value: RefCell<T>
}

#[derive(Default)]
pub struct PropertyManager {
    listeners: RefCell<HashMap<&'static str, Vec<Rc<dyn PropertyListener>>>>,
}

pub trait PropertyListener {
    fn fire(&self, source: SourcePtr, name: &str, old_value: *const (), new_value: *const ());
    fn before_change(&self, source: SourcePtr, property: &dyn Property);
    fn after_change(&self, source: SourcePtr, property: &dyn Property);
}

pub struct ControllerProperty<Pt: 'static> {
    property: Rc<TypedProperty<Pt>>,
    error_notifier: Option<Rc<dyn ErrorNotifier>>,
}

//// ****************************** ERROR HANDLING ******************************

pub type GuiExceptionPtr = Rc<GuiException>;

#[derive(Debug, Clone, PartialEq)]
pub struct GuiException {
    what: String,
}

pub struct GuiErrorToString;


pub type InputErrorProperty  = ControllerProperty<Option<GuiExceptionPtr>>;

//// ****************************** Bindings ******************************

/// property side callback that is called ba the component
pub trait ComponentLink<Ct> {
    fn set_value_from_component(&self, component: SourcePtr, value: Ct);
    fn reload_component_value(&self);
}

/// binding between chain and component
pub trait ComponentBinding<Ct> {
    fn add_component_value_change_listener(&self, listener: Weak<dyn ComponentLink<Ct>>);
    fn remove_component_value_change_listener(&self);
    fn set_component_value(&self, source: &dyn Property, value: Ct);
    fn get_component(&self) -> SourcePtr;
}

/// Interface used the the apop to control the behavior of the binding chain
pub trait BindingChainController {
    fn attach(&self);
    fn detach(&self);
    fn get_property(&self) -> Rc<dyn Property>;
    fn add_dependency(
        &self,
        dependency: Rc<dyn BindingChainDependency>,
    ) -> Rc<dyn BindingChainController>;
}


pub trait BindingChainDependency {
    /// anonymous weak reference
    fn as_any_weak(&self) -> Weak<dyn Any>;

    /// Registers the dependency. The dependency is already stored in the
    /// binding chain by the time this is called.
    fn register_dep(&self, chain: Weak<dyn BindingChainController>, myself: Weak<dyn Any>);
}


pub trait BindingConverter<Pt, Ct> {
    fn convert_component_value_to_property_value(
        &self,
        component_value: &Ct,
    ) -> Result<Pt, GuiException>;
    fn convert_property_value_to_component_value(&self, property_value: &Pt) -> Ct;
}

pub trait ErrorNotifier {
    fn set_error(&self, source: SourcePtr, error: &GuiException);
    fn clear_error(&self, source: SourcePtr);
}

/// Port of `action`.
pub trait Action {
    fn apply(&self, action: PropertyGroupActions, property: &dyn Property);
}

/// Port of `action_dependency<_T>`.
pub struct ActionDependency<T> {
    myself: RefCell<Weak<ActionDependency<T>>>,
    chain: RefCell<Weak<dyn BindingChainController>>,
    // Listeners are kept alive by the property manager
    listener: RefCell<Weak<PropertyListenerDispatcher>>,
    action: Rc<dyn Fn(PropertyGroupActions, &dyn Property)>,
    _marker: std::marker::PhantomData<T>,
}

// Port of `enum class property_group_actions`.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum PropertyGroupActions {
    BeforeFire,
    AfterFire,
}


pub struct IntToString;


