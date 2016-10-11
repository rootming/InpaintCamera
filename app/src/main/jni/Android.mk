LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE	:= STATIC

ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
  else
    include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
  endif
else
  include /home/rootming/PerfectCamera/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif

LOCAL_MODULE    := inpaint
LOCAL_SRC_FILES := jni_part.cpp rmmovobjs.cpp imagefix.cpp videostab.cpp inpainter.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
