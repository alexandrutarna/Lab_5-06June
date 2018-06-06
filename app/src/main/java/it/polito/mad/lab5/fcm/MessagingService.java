package it.polito.mad.lab5.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/*
 * Created by Mahmoud on 3/13/2017.
 */


public class MessagingService extends FirebaseMessagingService {

    // for logging ---------------------------------------
    String className = this.getClass().getSimpleName();
    String TAG = "--- " + className + " --- ";
    // ---------------------------------------------------


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //todo: handle notification
        Log.i(TAG, "onMessageReceived: received notification");



    }

}
