//
// Created by huzongyao on 2018/6/6.
//

#include <jni.h>
#include "MediaRenderer.h"
#include "DLNAServer.h"

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
    MediaRenderer *pRender = (MediaRenderer *) self;
    NPT_Result ret = pRender->Start(friendly_name, show_ip, uuid, (unsigned int) port, port_rebind);
    env->ReleaseStringUTFChars(friendly_name_, friendly_name);
    env->ReleaseStringUTFChars(uuid_, uuid);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jobject instance, jlong self) {
    MediaRenderer *pRender = (MediaRenderer *) self;
    return pRender->Stop();
}