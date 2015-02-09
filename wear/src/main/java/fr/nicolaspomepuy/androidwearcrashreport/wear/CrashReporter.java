package fr.nicolaspomepuy.androidwearcrashreport.wear;

import android.content.Context;
import android.content.Intent;

public class CrashReporter {
    private static CrashReporter INSTANCE;

    private final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            sendException(ex);
            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };


    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private Context context;

    private CrashReporter(Context context) {
        this.context = context;
    }

    public void start() {
        //Init the handler
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public static CrashReporter getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new CrashReporter(context);
        }
        return INSTANCE;
    }

    /**
     * Sends the exception to the handheld device
     *
     * @param throwable the {@code Throwable} to send
     */
    public void sendException(Throwable throwable) {
        Intent intent = new Intent(context, DataSyncIntentService.class);
        intent.putExtra(DataSyncIntentService.EXTRA_MESSAGE, Utils.serializeObject(throwable));
        context.startService(intent);
    }

}
