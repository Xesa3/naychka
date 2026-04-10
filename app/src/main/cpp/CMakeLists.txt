cmake_minimum_required(VERSION 3.22.1)

project("native-lib")

# OpenCV путь
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../opencv/sdk/native/jni)

find_package(OpenCV REQUIRED)

add_library(
        native-lib
        SHARED
        native-lib.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        native-lib
        ${OpenCV_LIBS}
        ${log-lib}
)