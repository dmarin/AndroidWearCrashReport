package fr.nicolaspomepuy.androidwearcrashreport.mobile;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by nicolas on 16/08/14.
 */
public class CrashReport implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    private IOnCrashListener onCrashListener;

    private static CrashReport INSTANCE;
    private CrashInfo currentCrashInfo;

    public static CrashReport getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new CrashReport(context);
        }
        return INSTANCE;
    }

    private CrashReport(Context context) {
        //Init the Google API client ot listen to crashes
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        //Add the listener to listen to data
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().contains("/EXCEPTION")) {

                //A new Exception has been received
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                CrashInfo crashInfo = CrashInfoGenerator.crashInfoGenerator(dataMapItem.getDataMap());

                //Send it with the listener
                if (crashInfo != null && onCrashListener != null) {
                    onCrashListener.onCrashReceived(crashInfo);
                }
            }
        }
    }

    public void setOnCrashListener(IOnCrashListener onCrashListener) {
        this.onCrashListener = onCrashListener;
    }

    //Listener interface
    public interface IOnCrashListener {
        void onCrashReceived(CrashInfo crashInfo);
    }

}
