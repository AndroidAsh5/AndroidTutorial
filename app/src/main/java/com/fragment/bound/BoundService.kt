package com.fragment.bound

import android.R
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.fragment.foregroundService.ForegroundActivity
import java.util.*

class BoundService : Service() {

    var TAG : String="BoundService"

    // Binder given to clients (notice class declaration below)
    private val mBinder: IBinder = MyBinder()


    // Random number generator
    private val mGenerator: Random = Random()

    // LiveData for capturing random number generated by the service
    val randomNumberLiveData: MutableLiveData<Int> = MutableLiveData()

    // Channel ID for notification
    val CHANNEL_ID = "Random number notification"

    override fun onBind(intent: Intent): IBinder {

       return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: " )
        createNotificationChannel()
        val notificationIntent = Intent(this, ForegroundActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bound Service")
            .setContentText("input")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification);

        Handler().postDelayed({
            val randomNumber = mGenerator.nextInt(100)
            randomNumberLiveData.postValue(randomNumber)
        }, 1000)
    }

    inner class MyBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: BoundService
            get() =// Return this instance of MyService so clients can call public methods
                this@BoundService
    }

    fun createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            Log.e(TAG, "start Channel: Service")
            var notificationChannel : NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "CHANNEL",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            var notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
