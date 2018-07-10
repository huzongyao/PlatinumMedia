//
// Created by tangbull on 2018/6/27.
//

#ifndef PLATINUMMEDIA_DLNABRIDGE_H
#define PLATINUMMEDIA_DLNABRIDGE_H

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

#define JNI_FUNC(x) Java_com_plutinosoft_platinum_DLNABridge_##x

#define CALLBACK_CLASS "com/plutinosoft/platinum/DLNABridge"
#define CALLBACK_METHOD "onNEvent"
#define CALLBACK_SIGN "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"

extern JavaVM *g_vm;
extern jclass g_callbackClass;
extern jmethodID g_callbackMethod;

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved);

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved);

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jclass type, jlong self,
                 jstring friendly_name_, jboolean show_ip,
                 jstring uuid_, jlong port, jboolean port_rebind);

JNIEXPORT jint JNICALL
JNI_FUNC(nExecute)(JNIEnv *env, jclass type, jlong self, jint cmd,
                   jstring param1_, jstring param2_,
                   jstring param3_);

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jclass type, jlong self);

JNIEXPORT jint JNICALL
JNI_FUNC(nDestroy)(JNIEnv *env, jclass type, jlong self);

#ifdef __cplusplus
}
#endif

#endif //PLATINUMMEDIA_DLNABRIDGE_H
