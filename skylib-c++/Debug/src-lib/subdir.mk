################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src-lib/converters.cpp \
../src-lib/property.cpp \
../src-lib/property_manager.cpp 

OBJS += \
./src-lib/converters.o \
./src-lib/property.o \
./src-lib/property_manager.o 

CPP_DEPS += \
./src-lib/converters.d \
./src-lib/property.d \
./src-lib/property_manager.d 


# Each subdirectory must supply rules for building sources it contributes
src-lib/%.o: ../src-lib/%.cpp src-lib/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++  -o "$@" "$<"   -std=c++2a -I/usr/include/sigc++-3.0 -I../src-gtk4-pilot -I../src-gtk4 -I../src-lib -I/usr/include/gio-unix-2.0 -I/usr/include/pangomm-2.48 -I/usr/lib/graphene-1.0/include/ -I/usr/include/graphene-1.0/ -I/usr/include/gtk-4.0 -I/usr/include/glibmm-2.68 -I/usr/include/giomm-2.68 -I/usr/include/glib-2.0 -I/usr/include/gtkmm-4.0 -O0 -g3 -Wall -c -fmessage-length=0  `pkg-config gtkmm-4.0 --cflags` -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@"
	@echo 'Finished building: $<'
	@echo ' '


