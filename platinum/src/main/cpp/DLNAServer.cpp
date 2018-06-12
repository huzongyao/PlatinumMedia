//
// Created by huzongyao on 2018/6/6.
//

#include <jni.h>
#include "MediaRenderer.h"
#include "DLNAServer.h"
#include "NdkLogger.h"

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jobject instance) {
    MediaRenderer *pRender = new MediaRenderer;
    return (jlong) pRender;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jobject instance, jlong self,
                 jstring friendly_name_, jboolean show_ip,
                 jstring uuid_, jlong port, jboolean port_rebind) {
    const char *friendly_name = env->GetStringUTFChars(friendly_name_, 0);
    const char *uuid = env->GetStringUTFChars(uuid_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        ret = pRender->Start(friendly_name, show_ip, uuid, (unsigned int) port, port_rebind);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(friendly_name_, friendly_name);
    env->ReleaseStringUTFChars(uuid_, uuid);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jobject instance, jlong self) {
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        ret = pRender->Stop();
    } else {
        LOGE("MediaRenderer is Null!");
    }
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nDestory)(JNIEnv *env, jobject instance, jlong self) {
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        delete pRender;
        ret = NPT_SUCCESS;
    } else {
        LOGE("MediaRenderer is Null!");
    }
    return ret;
}