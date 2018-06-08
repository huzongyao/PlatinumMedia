//
// Created by tangbull on 2018/6/6.
//

#ifndef PLATINUMMEDIA_DLNASERVER_H
#define PLATINUMMEDIA_DLNASERVER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

#define JNI_FUNC(x) Java_com_plutinosoft_platinum_DLNAServer_##x

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jobject instance);

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jobject instance, jlong self,
                 jstring friendly_name_, jboolean show_ip,
                 jstring uuid_, jlong port, jboolean port_rebind);

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jobject instance, jlong self);

#ifdef __cplusplus
}
#endif
#endif //PLATINUMMEDIA_DLNASERVER_H
