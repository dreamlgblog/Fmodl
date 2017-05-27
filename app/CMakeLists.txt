# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

set(lib_src_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
include_directories(
    ${CMAKE_SOURCE_DIR}/src/main/cpp/include
)
add_library(fmod SHARED IMPORTED)
set_target_properties(fmod PROPERTIES IMPORTED_LOCATION
        ${lib_src_DIR}/libfmod.so
)
add_library(fmodL SHARED IMPORTED)
set_target_properties(fmodL PROPERTIES IMPORTED_LOCATION
        ${lib_src_DIR}/libfmodL.so
)

add_library(  dream

               SHARED

               src/main/cpp/dream.cpp)



find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )



target_link_libraries( # Specifies the target library.
                       dream
                        fmodL
                        fmod
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
