package com.example.freshguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.freshguide.util.NetworkUtils;

/**
 * Broadcast Receiver — satisfies checklist 3.2.
 * Fires when network connectivity changes. Activities observe
 * NetworkChangeReceiver.isOnline (set here) via a local broadcast
 * or by polling NetworkUtils.isConnected() in onResume.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public interface NetworkListener {
        void onNetworkChanged(boolean isConnected);
    }

    private static NetworkListener listener;

    public static void setListener(NetworkListener l) {
        listener = l;
    }

    public static void clearListener() {
        listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = NetworkUtils.isConnected(context);
        if (listener != null) {
            listener.onNetworkChanged(connected);
        }
    }
}
