package com.reclycer.repertoire.data.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyRepertoireMessagingService: FirebaseMessagingService(){
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.let{
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d("Token", "From: " + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size > 0) {
                Log.d("Token", "Message data payload: " + remoteMessage.getData());



            }
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d("Token", "Message Notification Body: " + remoteMessage?.getNotification()?.getBody());
            }
        }



    }
}