//
// Created by huzongyao on 2026/8/7.
// DLNA DMC delegate: forwards discovered renderers and action results to Java.
//

#ifndef PLATINUMMEDIA_ANDMEDIACONTROLLER_H
#define PLATINUMMEDIA_ANDMEDIACONTROLLER_H

#include "Platinum.h"
#include "PltMediaController.h"

class AndUPnP;

/**
 * Bridges PLT_MediaControllerDelegate callbacks to the Java layer.
 * Device discovery events: {"type":"device","action":"added|removed","uuid":...,"name":...}
 * Action results are echoed back as {"type":"action","action":...,"code":...}
 */
class AndMediaController : public PLT_MediaControllerDelegate {
public:
    explicit AndMediaController(AndUPnP *upnp);

    ~AndMediaController() override;

    // PLT_MediaControllerDelegate
    bool OnMRAdded(PLT_DeviceDataReference &device) override;
    void OnMRRemoved(PLT_DeviceDataReference &device) override;

private:
    AndUPnP *mUpnp;

    void emitDevice(const char *action, PLT_DeviceDataReference &device);
};

#endif //PLATINUMMEDIA_ANDMEDIACONTROLLER_H
