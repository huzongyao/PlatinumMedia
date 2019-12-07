//
// Created by Administrator on 2019/11/22.
//

#ifndef PLATINUMCLIENT_PLATINUMAPI_H
#define PLATINUMCLIENT_PLATINUMAPI_H

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "NdkHelper.h"

#define JNI_FUNC(x) Java_com_plutinosoft_platinum_NativeUpnpApi_##x

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved);

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved);

JNIEXPORT jstring JNICALL
JNI_FUNC(getVersionString)(JNIEnv *env, jclass type);

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
JNI_FUNC(nSetCallback)(JNIEnv *env, jclass type, jlong iid, jobject callable);

JNIEXPORT jint JNICALL
JNI_FUNC(nAddRenderer)(JNIEnv *env, jclass type, jlong iid, jstring param_);

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jclass type, jlong iid);

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jclass type, jlong iid);

#ifdef __cplusplus
}
#endif

#endif //PLATINUMCLIENT_PLATINUMAPI_H
