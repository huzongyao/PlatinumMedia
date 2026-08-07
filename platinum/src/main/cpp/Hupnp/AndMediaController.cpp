//
// Created by huzongyao on 2026/8/7.
//

#include "AndMediaController.h"
#include "AndUPnP.h"

using namespace nlohmann;

AndMediaController::AndMediaController(AndUPnP *upnp) : mUpnp(upnp) {
}

AndMediaController::~AndMediaController() = default;

void AndMediaController::emitDevice(const char *action, PLT_DeviceDataReference &device) {
    if (!mUpnp || device.IsNull()) return;
    json j;
    j["type"] = "device";
    j["action"] = action;
    j["uuid"] = (const char *) device->GetUUID();
    j["name"] = (const char *) device->GetFriendlyName();
    mUpnp->notifyJEvent(j.dump());
}

bool AndMediaController::OnMRAdded(PLT_DeviceDataReference &device) {
    emitDevice("added", device);
    return true;
}

void AndMediaController::OnMRRemoved(PLT_DeviceDataReference &device) {
    emitDevice("removed", device);
}
