package fr.nicolaspomepuy.androidwearcrashreport.mobile;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public abstract class CrashReportWearableListenerService extends WearableListenerService {
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().contains("/EXCEPTION")) {

                //A new Exception has been received
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                CrashInfo crashInfo = CrashInfoGenerator.crashInfoGenerator(dataMapItem.getDataMap());

                //Send it with the listener
                if (crashInfo != null) {
                    onCrashReceived(crashInfo);
                }
            }
        }
    }

    protected abstract void onCrashReceived(CrashInfo crashInfo);
}
