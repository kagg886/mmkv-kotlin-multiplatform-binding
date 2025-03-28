cmake_minimum_required(VERSION 3.8.0)
project(mmkv_binding)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add MMKV as a subdirectory
add_subdirectory(../../../mmkv/POSIX/src mmkv)

# Create shared library
add_library(mmkv_binding SHARED src/native-binding-linux.cpp)

# Link against MMKV static library
target_link_libraries(mmkv_binding PRIVATE mmkv)

# Set output name to mmkvc.so
set_target_properties(mmkv_binding PROPERTIES OUTPUT_NAME "mmkvc")
