//
// Created by huzongyao on 2019/12/7.
//

#include "AndUPnP.h"
#include "NdkHelper.h"

using namespace nlohmann;

AndUPnP::AndUPnP() {
}

AndUPnP::~AndUPnP() {
}

int AndUPnP::AddRenderer(const char *param) {
    std::string name = "Platinum Media", uuid;
    bool showIp = false, portRebind = false;
    unsigned int port = 0;
    try {
        json j = json::parse(param);
        name = j["friendly_name"];
        showIp = j["show_ip"];
        uuid = j["uuid"];
        port = j["port"];
        portRebind = j["port_rebind"];
    } catch (json::exception &e) {
    }
    LOGD("AddRenderer(%s, %d, %s, %d, %d)", name.c_str(), showIp, uuid.c_str(), port, portRebind);
    PLT_DeviceHostReference renderer;
    renderer = new PLT_MediaRenderer(name.c_str(), showIp, uuid.c_str(), port, portRebind);
    return mUPnP.AddDevice(renderer);
}

int AndUPnP::SetJEventCallback(JNIEnv *env, jobject call, jmethodID mId) {
    DeleteJGlobalRefs(env);
    mJApiRef = env->NewGlobalRef(call);
    mJCallMethod = mId;
    return 0;
}

void AndUPnP::DeleteJGlobalRefs(JNIEnv *env) {
    if (mJApiRef) {
        env->DeleteGlobalRef(mJApiRef);
        mJApiRef = nullptr;
    }
}

int AndUPnP::Start() {
    return mUPnP.Start();
}

int AndUPnP::Stop() {
    return mUPnP.Stop();
}

std::string AndUPnP::notifyJEvent(std::string &param) {
    if (!g_Vm) {
        return "";
    }
    const char *newParam = param.c_str();
    JNIEnv *env = nullptr;
    auto status = g_Vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    auto attach = false;
    if (status != JNI_OK) {
        status = g_Vm->AttachCurrentThread(&env, nullptr);
        if (status < 0) {
            return "";
        }
        attach = true;
    }
    std::string ret;
    if (mJApiRef && mJCallMethod) {
        auto js = (jstring) env->CallObjectMethod(mJApiRef, mJCallMethod,
                                                  env->NewStringUTF(newParam));
        const char *cs = env->GetStringUTFChars(js, nullptr);
        LOGD("Call Java: (%s) -> [%s]", param.c_str(), cs);
        ret = std::string(cs);
        env->ReleaseStringUTFChars(js, cs);
    }
    if (attach) {
        g_Vm->DetachCurrentThread();
    }
    return ret;
}
