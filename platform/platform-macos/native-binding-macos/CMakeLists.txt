cmake_minimum_required(VERSION 3.10)
project(mmkv_binding)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
add_compile_definitions(FORCE_POSIX)

# Add MMKV as a subdirectory
add_subdirectory(../../MMKV_POSIX_FORK mmkv)

# Create shared library
add_library(mmkv_binding SHARED src/native-binding-macos.cpp)

# Link against MMKV static library
target_link_libraries(mmkv_binding PRIVATE mmkv)

# Set output name to mmkvc.so
set_target_properties(mmkv_binding PROPERTIES OUTPUT_NAME "mmkvc")
