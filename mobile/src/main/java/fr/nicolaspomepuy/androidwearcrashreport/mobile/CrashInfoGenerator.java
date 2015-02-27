package fr.nicolaspomepuy.androidwearcrashreport.mobile;

import com.google.android.gms.wearable.DataMap;

public class CrashInfoGenerator {

    public static CrashInfo crashInfoGenerator(DataMap dataMap){
        //Get the exception
        byte[] serializedException = dataMap.getByteArray("ex");
        Throwable throwable = (Throwable) Utils.deserializeObject(serializedException);
        if (throwable == null) {
            return null;
        }
        return new CrashInfo.Builder(throwable)
                .fingerprint(dataMap.getString("fingerprint"))
                .manufacturer(dataMap.getString("manufacturer"))
                .model(dataMap.getString("model"))
                .product(dataMap.getString("product"))
                .versionCode(dataMap.getInt("versionCode"))
                .versionName(dataMap.getString("versionName"))
                .osVersion(dataMap.getString("osVersion"))
                .packageName(dataMap.getString("packageName"))
                .build();
    }
}
