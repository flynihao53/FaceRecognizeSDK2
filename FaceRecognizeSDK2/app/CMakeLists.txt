# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS} -O0 -g -ggdb")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS} -O2")

set(POINTDETECTORNIR_ROOT_DIR src/main/cpp)

  add_library( lib_facedetctor SHARED IMPORTED )
set_target_properties(lib_facedetctor PROPERTIES
  IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libFaceDetector500.so)
add_library( lib_pointdetctor SHARED IMPORTED )
set_target_properties(lib_pointdetctor PROPERTIES
  IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libPointDetector403.so)

 add_library( # Sets the name of the library.
             FaceDetector500jni

             # Sets the library as a shared library.
            SHARED

             # Provides a relative path to your source file(s).
                src/main/cpp/FaceDetectorUtils.cpp )

add_library( # Sets the name of the library.
             PointDetector403jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/PointDetectorUtils.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
                                log-lib

                                # Specifies the name of the NDK library that
                                # you want CMake to locate.
                                log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

   target_include_directories(FaceDetector500jni PUBLIC
                            src/main/cpp
                           )
target_include_directories(PointDetector403jni PUBLIC
                            src/main/cpp
                            )

 target_link_libraries( # Specifies the target library.
                       FaceDetector500jni

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       lib_facedetctor)

target_link_libraries( # Specifies the target library.
                       PointDetector403jni

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       lib_pointdetctor)

set(LIB_OUTPUT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../Distribution/)
 set_target_properties(FaceDetector500jni PROPERTIES
                      LIBRARY_OUTPUT_DIRECTORY
                    ${LIB_OUTPUT_DIR}/${ANDROID_ABI})

set(LIB_OUTPUT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../Distribution/)
set_target_properties(PointDetector403jni PROPERTIES
                      LIBRARY_OUTPUT_DIRECTORY
                      ${LIB_OUTPUT_DIR}/${ANDROID_ABI})