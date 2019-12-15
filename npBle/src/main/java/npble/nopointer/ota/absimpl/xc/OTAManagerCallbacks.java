package npble.nopointer.ota.absimpl.xc;


import npble.nopointer.ota.absimpl.xc.no.nordicsemi.android.BleManagerCallbacks;

public abstract class OTAManagerCallbacks implements BleManagerCallbacks {
    public abstract void onOTAStart();
    public abstract void onProgress(float progress);
    public abstract void onOTARequestStart();
    public abstract void print(String toString);
}
