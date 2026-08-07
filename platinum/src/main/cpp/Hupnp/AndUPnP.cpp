//
// Created by huzongyao on 2019/12/7.
//

#include "AndUPnP.h"
#include "AndMediaController.h"
#include "NdkHelper.h"

using namespace nlohmann;

AndUPnP::AndUPnP() {
}

AndUPnP::~AndUPnP() {
    delete mRendererDelegate;
    delete mControllerDelegate;
    delete mController;
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

    mRenderer = new PLT_MediaRenderer(name.c_str(), showIp, uuid.c_str(), port, portRebind);

    // Attach the delegate so AVTransport/RenderingControl commands are forwarded to Java.
    if (!mRendererDelegate) {
        mRendererDelegate = new AndMediaRenderer(this, uuid.c_str());
    }
    ((PLT_MediaRenderer *) mRenderer.AsPointer())->SetDelegate(mRendererDelegate);

    return mUPnP.AddDevice(mRenderer);
}

int AndUPnP::AddController(const char *param) {
    bool showIp = false;
    unsigned int port = 0;
    bool portRebind = false;
    try {
        json j = json::parse(param);
        showIp = j["show_ip"];
        port = j["port"];
        portRebind = j["port_rebind"];
    } catch (json::exception &e) {
    }
    LOGD("AddController(%d, %d, %d)", showIp, port, portRebind);

    if (mController) {
        return NPT_SUCCESS;
    }

    // PLT_CtrlPoint only takes a search criteria string; discovery port/ip are
    // managed internally by the UPnP stack.
    PLT_CtrlPointReference ctrlPoint(new PLT_CtrlPoint("upnp:rootdevice"));
    mControllerDelegate = new AndMediaController(this);
    mController = new PLT_MediaController(ctrlPoint, mControllerDelegate);
    ctrlPoint->AddListener(mController);

    return mUPnP.AddCtrlPoint(ctrlPoint);
}

int AndUPnP::InvokeAction(const char *param) {
    if (!mController) {
        LOGE("InvokeAction: no controller");
        return NPT_FAILURE;
    }
    try {
        json j = json::parse(param);
        std::string uuid = j["uuid"];
        std::string action = j["action"];
        unsigned int instanceId = j.value("instance_id", 0);

        PLT_DeviceDataReference device;
        NPT_Result res = mController->FindRenderer(uuid.c_str(), device);
        if (NPT_FAILED(res) || device.IsNull()) {
            LOGE("InvokeAction: renderer %s not found", uuid.c_str());
            return NPT_FAILURE;
        }

        NPT_String speed = j.value("speed", std::string("1")).c_str();
        NPT_String uri = j.value("uri", std::string("")).c_str();
        NPT_String metadata = j.value("metadata", std::string("")).c_str();
        NPT_String target = j.value("target", std::string("")).c_str();
        NPT_String unit = j.value("unit", std::string("REL_TIME")).c_str();
        NPT_String channel = j.value("channel", std::string("Master")).c_str();
        int volume = j.value("volume", 0);
        bool mute = j.value("mute", false);

        if (action == "SetAVTransportURI") {
            return mController->SetAVTransportURI(device, instanceId, uri, metadata, nullptr);
        } else if (action == "Play") {
            return mController->Play(device, instanceId, speed, nullptr);
        } else if (action == "Pause") {
            return mController->Pause(device, instanceId, nullptr);
        } else if (action == "Stop") {
            return mController->Stop(device, instanceId, nullptr);
        } else if (action == "Seek") {
            return mController->Seek(device, instanceId, unit, target, nullptr);
        } else if (action == "Next") {
            return mController->Next(device, instanceId, nullptr);
        } else if (action == "Previous") {
            return mController->Previous(device, instanceId, nullptr);
        } else if (action == "SetVolume") {
            return mController->SetVolume(device, instanceId, channel, volume, nullptr);
        } else if (action == "SetMute") {
            return mController->SetMute(device, instanceId, channel, mute, nullptr);
        }
        LOGE("InvokeAction: unknown action %s", action.c_str());
        return NPT_FAILURE;
    } catch (json::exception &e) {
        LOGE("InvokeAction parse error: %s", e.what());
        return NPT_FAILURE;
    }
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

std::string AndUPnP::notifyJEvent(const std::string &param) {
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
        // The Java onEvent may return null (or throw); handle both safely.
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
            js = nullptr;
        }
        if (js != nullptr) {
            const char *cs = env->GetStringUTFChars(js, nullptr);
            LOGD("Call Java: (%s) -> [%s]", param.c_str(), cs);
            ret = std::string(cs);
            env->ReleaseStringUTFChars(js, cs);
            env->DeleteLocalRef(js);
        } else {
            LOGD("Call Java: (%s) -> [null]", param.c_str());
        }
    }
    if (attach) {
        g_Vm->DetachCurrentThread();
    }
    return ret;
}
