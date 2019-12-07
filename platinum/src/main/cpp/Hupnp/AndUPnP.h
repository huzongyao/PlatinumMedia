//
// Created by huzongyao on 2019/12/7.
//

#ifndef PLATINUMMEDIA_ANDUPNP_H
#define PLATINUMMEDIA_ANDUPNP_H

#include <jni.h>
#include "json.hpp"
#include "Platinum.h"

extern JavaVM *g_Vm;

class AndUPnP {
public:
    AndUPnP();

    virtual ~AndUPnP();

    int AddRenderer(const char *param);

    int SetJEventCallback(JNIEnv *env, jobject call, jmethodID mId);

    void DeleteJGlobalRefs(JNIEnv *env);

    int Start();

    int Stop();

    std::string notifyJEvent(std::string &param);

private:
    PLT_UPnP mUPnP;

    jobject mJApiRef = nullptr;
    jmethodID mJCallMethod = nullptr;
};


#endif //PLATINUMMEDIA_ANDUPNP_H
