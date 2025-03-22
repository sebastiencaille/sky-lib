################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src-test/GtkTest.cpp 

CPP_DEPS += \
./src-test/GtkTest.d 

OBJS += \
./src-test/GtkTest.o 


# Each subdirectory must supply rules for building sources it contributes
src-test/GtkTest.o: ../src-test/GtkTest.cpp src-test/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++  -o "$@" "$<"   -std=c++20 -I/usr/include/sigc++-3.0 -I../src-gtk4-pilot -I../src-gtk4 -I../src-lib -I/usr/include/gio-unix-2.0 -I/usr/include/pangomm-2.48 -I/usr/lib/graphene-1.0/include/ -I/usr/include/graphene-1.0/ -I/usr/include/gtk-4.0 -I/usr/include/glibmm-2.68 -I/usr/include/giomm-2.68 -I/usr/include/glib-2.0 -I/usr/include/gtkmm-4.0 -O0 -g3 -Wall -c -fmessage-length=0  `pkg-config gtkmm-4.0 --cflags` -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@"
	@echo 'Finished building: $<'
	@echo ' '


clean: clean-src-2d-test

clean-src-2d-test:
	-$(RM) ./src-test/GtkTest.d ./src-test/GtkTest.o

.PHONY: clean-src-2d-test

