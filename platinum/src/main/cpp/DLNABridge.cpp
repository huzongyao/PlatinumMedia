//
// Created by huzongyao on 2018/6/27.
//

#include "DLNABridge.h"

#include <jni.h>
#include "MediaRenderer.h"
#include "NdkLogger.h"
#include "DLNAServer.h"

JavaVM *g_vm = NULL;
jclass g_callbackClass = NULL;
jmethodID g_callbackMethod = NULL;

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    NPT_LogManager::GetDefault().Configure(
            "plist:.level=INFO;.handlers=ConsoleHandler;.ConsoleHandler.outputs=2;"
                    ".ConsoleHandler.colors=false;.ConsoleHandler.filter=59");
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
    DLNAServer *pServer = new DLNAServer;
    return (jlong) pServer;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jclass type, jlong self,
                 jstring friendly_name_, jboolean show_ip,
                 jstring uuid_, jlong port, jboolean port_rebind) {
    const char *friendly_name = env->GetStringUTFChars(friendly_name_, 0);
    const char *uuid = env->GetStringUTFChars(uuid_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        DLNAServer *pServer = (DLNAServer *) self;
        ret = pServer->Start(friendly_name, show_ip, uuid, (unsigned int) port, port_rebind);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(friendly_name_, friendly_name);
    env->ReleaseStringUTFChars(uuid_, uuid);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nExecute)(JNIEnv *env, jclass type, jlong self, jint cmd, jstring param1_,
                   jstring param2_, jstring param3_) {
    const char *param1 = env->GetStringUTFChars(param1_, 0);
    const char *param2 = env->GetStringUTFChars(param2_, 0);
    const char *param3 = env->GetStringUTFChars(param3_, 0);
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        DLNAServer *pServer = (DLNAServer *) self;
        //ret = pRender->SetMediaDuration(duration);
    } else {
        LOGE("MediaRenderer is Null!");
    }
    env->ReleaseStringUTFChars(param1_, param1);
    env->ReleaseStringUTFChars(param2_, param2);
    env->ReleaseStringUTFChars(param3_, param3);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jclass type, jlong self) {
    NPT_Result ret = NPT_ERROR_INVALID_STATE;
    if (self != 0L) {
        DLNAServer *pServer = (DLNAServer *) self;
        //ret = pRender->Stop();
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
        DLNAServer *pServer = (DLNAServer *) self;
        delete pServer;
        ret = NPT_SUCCESS;
    } else {
        LOGE("MediaRenderer is Null!");
    }
    return ret;
}