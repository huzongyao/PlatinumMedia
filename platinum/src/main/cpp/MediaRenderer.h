//
// Created by huzongyao on 2018/6/6.
//

#ifndef PLATINUMMEDIA_MEDIARENDER_H
#define PLATINUMMEDIA_MEDIARENDER_H


#include <Platinum.h>
#include <PltMediaRenderer.h>
#include <jni.h>

class MediaRenderer : public PLT_MediaRenderer {
public:
    MediaRenderer(const char *friendly_name,
                  bool show_ip = false,
                  const char *uuid = NULL,
                  unsigned int port = 0,
                  bool port_rebind = false);

    ~MediaRenderer() override;

    // Http server handler
    NPT_Result
    ProcessHttpGetRequest(NPT_HttpRequest &request, const NPT_HttpRequestContext &context,
                          NPT_HttpResponse &response) override;

    // AVTransport methods
    NPT_Result OnNext(PLT_ActionReference &action) override;

    NPT_Result OnPause(PLT_ActionReference &action) override;

    NPT_Result OnPlay(PLT_ActionReference &action) override;

    NPT_Result OnPrevious(PLT_ActionReference &action) override;

    NPT_Result OnStop(PLT_ActionReference &action) override;

    NPT_Result OnSeek(PLT_ActionReference &action) override;

    NPT_Result OnSetAVTransportURI(PLT_ActionReference &action) override;

    // RenderingControl methods
    NPT_Result OnSetVolume(PLT_ActionReference &action) override;

    NPT_Result OnSetMute(PLT_ActionReference &action) override;

private:
    NPT_Result SetupServices() override;

    NPT_Result DoJavaCallback(int type, const char *param1 = "",
                              const char *param2 = "",
                              const char *param3 = "");
};


#endif //PLATINUMMEDIA_MEDIARENDER_H
