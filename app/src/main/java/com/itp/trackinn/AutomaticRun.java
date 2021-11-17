package com.itp.trackinn;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.itp.trackinn.ServiceGeolocation.ServiceGeolocation;

public class AutomaticRun extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean flg_activate=false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo services : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ServiceGeolocation.class.getName().equals(services.service.getClassName())) {
                flg_activate=true;
            }
        }
        if(!flg_activate){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                Intent service = new Intent(context,  ServiceGeolocation.class);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startForegroundService(service);
            }
            else {
                Intent service = new Intent(context,  ServiceGeolocation.class);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(service);
            }

        }
    }
}
