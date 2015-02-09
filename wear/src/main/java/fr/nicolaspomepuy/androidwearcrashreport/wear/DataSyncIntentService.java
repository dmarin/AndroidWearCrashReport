package fr.nicolaspomepuy.androidwearcrashreport.wear;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class DataSyncIntentService extends IntentService {
    public static final String EXTRA_MESSAGE = "extraMessage";

    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient googleApiClient;

    public DataSyncIntentService() {
        super(DataSyncIntentService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

        // Collection version info
        PackageInfo pInfo;
        int versionCode = 0;
        String versionName = "";
        String packageName = context.getPackageName();
        try {
            pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionCode = pInfo.versionCode;
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        // Send the Throwable
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(MessagingPathes.EXCEPTION + System.currentTimeMillis());
        DataMap dataMap = dataMapRequest.getDataMap();
        dataMap.putByteArray("ex", intent.getByteArrayExtra(EXTRA_MESSAGE));
        // Add a bit of information on the Wear Device to pass a long with the exception
        dataMap.putString("board", Build.BOARD);
        dataMap.putString("fingerprint", Build.FINGERPRINT);
        dataMap.putString("model", Build.MODEL);
        dataMap.putString("manufacturer", Build.MANUFACTURER);
        dataMap.putString("product", Build.PRODUCT);
        dataMap.putString("versionName", versionName);
        dataMap.putInt("versionCode", versionCode);
        dataMap.putString("osVersion", Build.VERSION.RELEASE);
        dataMap.putString("packageName", packageName);
        PutDataRequest request = dataMapRequest.asPutDataRequest();

        Wearable.DataApi.putDataItem(googleApiClient, request).await();

        googleApiClient.disconnect();
    }

}