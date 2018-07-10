//
// Created by huzongyao on 2018/6/6.
//

#include <Neptune.h>
#include "MediaRenderer.h"
#include "NdkLogger.h"
#include "DLNABridge.h"
#include "CallbackTypes.h"

NPT_SET_LOCAL_LOGGER("MediaRenderer")

MediaRenderer::MediaRenderer(const char *friendly_name,
                             bool show_ip     /* = false */,
                             const char *uuid        /* = NULL */,
                             unsigned int port        /* = 0 */,
                             bool port_rebind /* = false */)
        : PLT_MediaRenderer(friendly_name, show_ip, uuid, port) {
    LOGD("MediaRenderer::MediaRenderer");
    LOGD("Name[%s], Show IP[%d], UUID[%s], Port[%u], Port Rebind[%d]",
         friendly_name, show_ip, uuid, port, port_rebind);
}


MediaRenderer::~MediaRenderer() {
    LOGD("MediaRenderer::~MediaRenderer()");
}

NPT_Result MediaRenderer::SetupServices() {
    LOGD("MediaRenderer::SetupServices()");
    NPT_CHECK(PLT_MediaRenderer::SetupServices());
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnNext(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnNext()");
    NPT_String uri, meta;
    PLT_Service *service;
    NPT_CHECK_SEVERE(FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", service));
    NPT_CHECK_SEVERE(action->GetArgumentValue("NextURI", uri));
    NPT_CHECK_SEVERE(action->GetArgumentValue("NextURIMetaData", meta));
    service->SetStateVariable("NextAVTransportURI", uri);
    service->SetStateVariable("NextAVTransportURIMetaData", meta);
    NPT_CHECK_SEVERE(action->SetArgumentsOutFromStateVariable());
    DoJavaCallback(CALLBACK_EVENT_ON_NEXT, uri, meta);
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnPause(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnPause()");
    PLT_Service *service;
    DoJavaCallback(CALLBACK_EVENT_ON_PAUSE);
    NPT_CHECK_SEVERE(FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", service));
    service->SetStateVariable("TransportState", "PAUSED_PLAYBACK");
    service->SetStateVariable("TransportStatus", "OK");
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnPrevious(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnPrevious()");
    DoJavaCallback(CALLBACK_EVENT_ON_PREVIOUS);
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnStop(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnStop()");
    PLT_Service *service;
    DoJavaCallback(CALLBACK_EVENT_ON_STOP);
    NPT_CHECK_SEVERE(FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", service));
    service->SetStateVariable("TransportState", "STOPPED");
    service->SetStateVariable("TransportStatus", "OK");
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnPlay(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnPlay()");
    NPT_String uri, meta;
    PLT_Service *service;
    // look for value set previously by SetAVTransportURI
    NPT_CHECK_SEVERE(FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", service));
    NPT_CHECK_SEVERE(service->GetStateVariableValue("AVTransportURI", uri));
    NPT_CHECK_SEVERE(service->GetStateVariableValue("AVTransportURIMetaData", meta));
    // if not set, use the current file being played
    service->SetStateVariable("TransportState", "TRANSITIONING");
    service->SetStateVariable("TransportStatus", "OK");
    // parse meta data and play media
    DoJavaCallback(CALLBACK_EVENT_ON_PLAY, uri, meta);
    // just return success because the play actions are asynchronous
    service->SetStateVariable("TransportState", "PLAYING");
    service->SetStateVariable("TransportStatus", "OK");
    service->SetStateVariable("AVTransportURI", uri);
    service->SetStateVariable("AVTransportURIMetaData", meta);
    service->SetStateVariable("NextAVTransportURI", "");
    service->SetStateVariable("NextAVTransportURIMetaData", "");
    if (&action) {
        NPT_CHECK_SEVERE(action->SetArgumentsOutFromStateVariable());
    }
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnSeek(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnSeek()");
    NPT_String unit, target;
    NPT_CHECK_SEVERE(action->GetArgumentValue("Unit", unit));
    NPT_CHECK_SEVERE(action->GetArgumentValue("Target", target));
    if (!unit.Compare("REL_TIME")) {
        // converts target to seconds
        NPT_UInt32 seconds;
        NPT_CHECK_SEVERE(PLT_Didl::ParseTimeStamp(target, seconds));
        const char *secondString = NPT_String::FromInteger(seconds).GetChars();
        DoJavaCallback(CALLBACK_EVENT_ON_SEEK, unit, target, secondString);
    }
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnSetAVTransportURI(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnSetAVTransportURI()");
    NPT_String uri, meta;
    PLT_Service *service;
    NPT_CHECK_SEVERE(FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", service));
    NPT_CHECK_SEVERE(action->GetArgumentValue("CurrentURI", uri));
    NPT_CHECK_SEVERE(action->GetArgumentValue("CurrentURIMetaData", meta));
    // if not playing already, just keep around uri & metadata
    // and wait for play command
    service->SetStateVariable("TransportState", "STOPPED");
    service->SetStateVariable("TransportStatus", "OK");
    service->SetStateVariable("TransportPlaySpeed", "1");
    service->SetStateVariable("AVTransportURI", uri);
    service->SetStateVariable("AVTransportURIMetaData", meta);
    service->SetStateVariable("NextAVTransportURI", "");
    service->SetStateVariable("NextAVTransportURIMetaData", "");
    NPT_CHECK_SEVERE(action->SetArgumentsOutFromStateVariable());
    DoJavaCallback(CALLBACK_EVENT_ON_SET_AV_TRANSPORT_URI, uri.GetChars(), meta.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnSetVolume(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnSetVolume()");
    NPT_String volume;
    NPT_CHECK_SEVERE(action->GetArgumentValue("DesiredVolume", volume));
    DoJavaCallback(CALLBACK_EVENT_ON_SET_VOLUME, volume.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::OnSetMute(PLT_ActionReference &action) {
    LOGD("MediaRenderer::OnSetMute()");
    NPT_String mute;
    NPT_CHECK_SEVERE(action->GetArgumentValue("DesiredMute", mute));
    DoJavaCallback(CALLBACK_EVENT_ON_SET_MUTE, mute.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaRenderer::ProcessHttpGetRequest(NPT_HttpRequest &request,
                                                const NPT_HttpRequestContext &context,
                                                NPT_HttpResponse &response) {
    // get the address of who sent us some data back
    NPT_String ip_address = context.GetRemoteAddress().GetIpAddress().ToString();
    NPT_String method = request.GetMethod();
    NPT_String protocol = request.GetProtocol();
    NPT_HttpUrl url = request.GetUrl();
    LOGD("Http: IP: %s\nMethod: %s\nProtocol: %s\nUrl: %s",
         ip_address.GetChars(), method.GetChars(), protocol.GetChars(), url.ToString().GetChars());
    return PLT_DeviceHost::ProcessHttpGetRequest(request, context, response);
}

NPT_Result MediaRenderer::DoJavaCallback(int type, const char *param1,
                                         const char *param2,
                                         const char *param3) {
    if (g_vm == NULL) {
        LOGE("g_vm = NULL!!!");
        return NPT_FAILURE;
    }
    int status;
    JNIEnv *env = NULL;
    bool isAttach = false;
    status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status != JNI_OK) {
        status = g_vm->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            LOGE("MediaRenderer::DoJavaCallback Failed: %d", status);
            return NPT_FAILURE;
        }
        isAttach = true;
    }
    jstring jParam1 = NULL;
    jstring jParam2 = NULL;
    jstring jParam3 = NULL;
    jclass inflectClass = g_callbackClass;
    jmethodID inflectMethod = g_callbackMethod;
    if (inflectClass == NULL || inflectMethod == NULL) {
        goto end;
    }
    jParam1 = env->NewStringUTF(param1);
    jParam2 = env->NewStringUTF(param2);
    jParam3 = env->NewStringUTF(param3);
    env->CallStaticVoidMethod(inflectClass, inflectMethod, type, jParam1, jParam2, jParam3);
    env->DeleteLocalRef(jParam1);
    env->DeleteLocalRef(jParam2);
    env->DeleteLocalRef(jParam3);
    end:
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (isAttach) {
        g_vm->DetachCurrentThread();
    }
    return NPT_SUCCESS;
}


