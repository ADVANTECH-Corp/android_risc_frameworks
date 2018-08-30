LOCAL_PATH := $(call my-dir)

common_src_files := vnc.c


include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    advdaemon.c \
    $(common_src_files)

LOCAL_SHARED_LIBRARIES := \
    libcutils \
    liblog \
    libselinux

LOCAL_MODULE := advservice

LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
