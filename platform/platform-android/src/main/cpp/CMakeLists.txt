cmake_minimum_required(VERSION 3.22.1)

project("mmkvc")

add_subdirectory(../../../../../MMKV/Core core)

add_library(${CMAKE_PROJECT_NAME} SHARED platform.cpp)
target_link_libraries(${CMAKE_PROJECT_NAME} android log core)