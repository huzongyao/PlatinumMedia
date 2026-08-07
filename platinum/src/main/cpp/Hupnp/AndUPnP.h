//
// Created by huzongyao on 2019/12/7.
//

#ifndef PLATINUMMEDIA_ANDUPNP_H
#define PLATINUMMEDIA_ANDUPNP_H

#include <jni.h>
#include "json.hpp"
#include "Platinum.h"
#include "AndMediaRenderer.h"

extern JavaVM *g_Vm;

class AndUPnP {
public:
    AndUPnP();

    virtual ~AndUPnP();

    int AddRenderer(const char *param);

    /** Add a DMC (MediaController) to discover renderers and send cast commands. */
    int AddController(const char *param);

    /** Send an AVTransport/RenderingControl command to a discovered renderer (DMC side). */
    int InvokeAction(const char *param);

    int SetJEventCallback(JNIEnv *env, jobject call, jmethodID mId);

    void DeleteJGlobalRefs(JNIEnv *env);

    int Start();

    int Stop();

    std::string notifyJEvent(const std::string &param);

private:
    PLT_UPnP mUPnP;

    PLT_DeviceHostReference mRenderer;
    PLT_MediaRendererDelegate *mRendererDelegate = nullptr;

    PLT_MediaController *mController = nullptr;
    PLT_MediaControllerDelegate *mControllerDelegate = nullptr;

    jobject mJApiRef = nullptr;
    jmethodID mJCallMethod = nullptr;
};


#endif //PLATINUMMEDIA_ANDUPNP_H
