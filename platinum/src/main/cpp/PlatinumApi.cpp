//
// Created by huzongyao on 2019/11/22.
//

#include <Platinum.h>
#include "AndUPnP.h"
#include "PlatinumApi.h"

JavaVM *g_Vm = nullptr;

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_Vm = vm;
    NPT_LogManager::GetDefault().Configure(
            "plist:.level=INFO;.handlers=ConsoleHandler;.ConsoleHandler.outputs=2;"
            ".ConsoleHandler.colors=false;.ConsoleHandler.filter=59");
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    g_Vm = nullptr;
}

JNIEXPORT jstring JNICALL
JNI_FUNC(getVersionString)(JNIEnv *env, jclass type) {
    return env->NewStringUTF(PLT_PLATINUM_SDK_VERSION_STRING);
}

JNIEXPORT jlong JNICALL
JNI_FUNC(nInit)(JNIEnv *env, jclass type) {
    auto *p = new AndUPnP;
    return (jlong) p;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nAddRenderer)(JNIEnv *env, jclass type, jlong iid, jstring param_) {
    const char *param = env->GetStringUTFChars(param_, nullptr);
    int ret = 0;
    if (iid) {
        auto *p = (AndUPnP *) iid;
        ret = p->AddRenderer(param);
    }
    env->ReleaseStringUTFChars(param_, param);
    return ret;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nSetCallback)(JNIEnv *env, jclass type, jlong iid, jobject callable) {
    if (iid) {
        auto *p = (AndUPnP *) iid;
        jmethodID methodId = env->GetMethodID(type, "onEvent",
                                              "(Ljava/lang/String;)Ljava/lang/String;");
        p->SetJEventCallback(env, callable, methodId);
    }
    return 0;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStart)(JNIEnv *env, jclass type, jlong iid) {
    if (iid) {
        auto *p = (AndUPnP *) iid;
        p->Start();
    }
    return 0;
}

JNIEXPORT jint JNICALL
JNI_FUNC(nStop)(JNIEnv *env, jclass type, jlong iid) {
    if (iid) {
        auto *p = (AndUPnP *) iid;
        p->Stop();
    }
    return 0;
}