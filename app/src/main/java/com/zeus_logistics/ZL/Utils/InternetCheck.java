package com.zeus_logistics.ZL.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.NetworkInterface;

public class InternetCheck {
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null) {
                for(int i=0;i<info.length;i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
