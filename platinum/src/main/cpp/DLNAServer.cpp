//
// Created by huzongyao on 2018/6/6.
//

#include <jni.h>
#include "MediaRenderer.h"
#include "DLNAServer.h"
#include "NdkLogger.h"

JavaVM *g_vm = NULL;
jclass g_callbackClass = NULL;
jmethodID g_callbackMethod = NULL;

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGW("JNI_OnLoad!");
    g_vm = vm;
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGW("JNI_OnUnload!");
}

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jclass type) {
    jclass myclass = env->FindClass(CALLBACK_CLASS);
    g_callbackClass = (jclass) env->NewGlobalRef(myclass);
    g_callbackMethod = env->GetStaticMethodID(g_callbackClass, CALLBACK_METHOD, CALLBACK_SIGN);
    MediaRenderer *pRender = new MediaRenderer();
    return (jlong) pRender;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jclass type, jlong self,
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
JNI_FUNC(nSetMediaDuration)(JNIEnv *env, jclass type, jlong self,
                            jstring duration_) {
    const char *duration = env->GetStringUTFChars(duration_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        ret = pRender->SetMediaDuration(duration);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(duration_, duration);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nSetTimePosition)(JNIEnv *env, jclass type, jlong self,
                           jstring position_) {
    const char *position = env->GetStringUTFChars(position_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        ret = pRender->SetTimePosition(position);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(position_, position);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nSetTransportState)(JNIEnv *env, jclass type, jlong self,
                             jstring state_) {
    const char *state = env->GetStringUTFChars(state_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        ret = pRender->SetTransportState(state);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(state_, state);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jclass type, jlong self) {
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
JNI_FUNC(nDestroy)(JNIEnv *env, jclass type, jlong self) {
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (g_callbackClass != NULL) {
        env->DeleteGlobalRef(g_callbackClass);
        g_callbackClass = NULL;
    }
    if (self != 0L) {
        MediaRenderer *pRender = (MediaRenderer *) self;
        delete pRender;
        ret = NPT_SUCCESS;
    } else {
        LOGE("MediaRenderer is Null!");
    }
    return ret;
}