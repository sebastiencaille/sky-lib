################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src-gtk4/glib_converter.cpp \
../src-gtk4/gtk_bindings.cpp \
../src-gtk4/gtk_utils.cpp 

CPP_DEPS += \
./src-gtk4/glib_converter.d \
./src-gtk4/gtk_bindings.d \
./src-gtk4/gtk_utils.d 

OBJS += \
./src-gtk4/glib_converter.o \
./src-gtk4/gtk_bindings.o \
./src-gtk4/gtk_utils.o 


# Each subdirectory must supply rules for building sources it contributes
src-gtk4/%.o: ../src-gtk4/%.cpp src-gtk4/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++  -o "$@" "$<"   -std=c++20 -I/usr/include/sigc++-3.0 -I../src-gtk4-pilot -I../src-gtk4 -I../src-lib -I/usr/include/gio-unix-2.0 -I/usr/include/pangomm-2.48 -I/usr/lib/graphene-1.0/include/ -I/usr/include/graphene-1.0/ -I/usr/include/gtk-4.0 -I/usr/include/glibmm-2.68 -I/usr/include/giomm-2.68 -I/usr/include/glib-2.0 -I/usr/include/gtkmm-4.0 -O0 -g3 -Wall -c -fmessage-length=0  `pkg-config gtkmm-4.0 --cflags` -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@"
	@echo 'Finished building: $<'
	@echo ' '


clean: clean-src-2d-gtk4

clean-src-2d-gtk4:
	-$(RM) ./src-gtk4/glib_converter.d ./src-gtk4/glib_converter.o ./src-gtk4/gtk_bindings.d ./src-gtk4/gtk_bindings.o ./src-gtk4/gtk_utils.d ./src-gtk4/gtk_utils.o

.PHONY: clean-src-2d-gtk4

