################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src-gtk4-pilot/abstract_gtk_component.cpp \
../src-gtk4-pilot/abstract_gui_component.cpp \
../src-gtk4-pilot/gtk_entry_pilot.cpp \
../src-gtk4-pilot/gtk_gui_pilot.cpp 

OBJS += \
./src-gtk4-pilot/abstract_gtk_component.o \
./src-gtk4-pilot/abstract_gui_component.o \
./src-gtk4-pilot/gtk_entry_pilot.o \
./src-gtk4-pilot/gtk_gui_pilot.o 

CPP_DEPS += \
./src-gtk4-pilot/abstract_gtk_component.d \
./src-gtk4-pilot/abstract_gui_component.d \
./src-gtk4-pilot/gtk_entry_pilot.d \
./src-gtk4-pilot/gtk_gui_pilot.d 


# Each subdirectory must supply rules for building sources it contributes
src-gtk4-pilot/%.o: ../src-gtk4-pilot/%.cpp src-gtk4-pilot/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++  -o "$@" "$<"   -std=c++2a -I/usr/include/sigc++-3.0 -I/usr/include/gio-unix-2.0 -I/usr/include/pangomm-2.48 -I/usr/lib/graphene-1.0/include/ -I/usr/include/graphene-1.0/ -I/usr/include/gtk-4.0 -I/usr/include/glibmm-2.68 -I/usr/include/giomm-2.68 -I/usr/include/glib-2.0 -I/usr/include/gtkmm-4.0 -I"/home/scaille/src/github/sky-lib/skylib-c++/src-lib" -I"/home/scaille/src/github/sky-lib/skylib-c++/src-gtk4" -I"/home/scaille/src/github/sky-lib/skylib-c++/src-gtk4-pilot" -O0 -g3 -Wall -c -fmessage-length=0  `pkg-config gtkmm-4.0 --cflags` -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@"
	@echo 'Finished building: $<'
	@echo ' '


