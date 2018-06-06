# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.
# by huzongyao

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DNPT_CONFIG_HAVE_SYSTEM_LOG_CONFIG")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

# Neptune-1.2.3
# Platinum-1.2.0

include_directories(Platinum/Platinum
        Platinum/Core
        Platinum/Devices/MediaServer
        Platinum/Devices/MediaConnect
        Platinum/Devices/MediaRenderer
        Platinum/Extras
        Neptune/Core)

file(GLOB_RECURSE NATIVE_SRCS "*.cpp")

add_library(platinum-jni SHARED ${NATIVE_SRCS})

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries(platinum-jni log android)
