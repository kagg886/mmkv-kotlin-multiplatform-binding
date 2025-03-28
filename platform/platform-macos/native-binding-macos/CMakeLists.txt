cmake_minimum_required(VERSION 3.8.0)
project(mmkv_binding)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set macOS specific options
set(CMAKE_MACOSX_RPATH 1)
set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)
set(CMAKE_INSTALL_RPATH "@executable_path")

# Add MMKV as a subdirectory
add_subdirectory(../../../mmkv/POSIX/src mmkv)

# Create shared library
add_library(mmkv_binding SHARED src/native-binding-macos.cpp)

# Include MMKV headers
target_include_directories(mmkv_binding PRIVATE ../../../mmkv/Core)

# Link against MMKV static library - make sure to include both libraries
target_link_libraries(mmkv_binding PRIVATE mmkv core)

# Set output name to mmkvc.dylib (macOS uses .dylib instead of .so)
set_target_properties(mmkv_binding PROPERTIES 
    OUTPUT_NAME "mmkvc"
    PREFIX "lib"
    SUFFIX ".dylib")