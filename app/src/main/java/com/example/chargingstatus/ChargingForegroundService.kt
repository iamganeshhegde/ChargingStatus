package com.example.chargingstatus

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class ChargingForegroundService: Service() {


    val NOTIFICATION_ID = 0;
    val CHANNEL_ID = "ForegroundServiceChannel"
    var deviceStatus =0
     var notification:Notification? = null

    var intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    var changeBatteryBroadcast = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent != null) {
                deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1)
            }

            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
            val batteryLevel = (level!!.toFloat() / scale!!.toFloat() * 100.0f).toInt()

            var builder = context?.let { NotificationCompat.Builder(it,CHANNEL_ID) };

            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                notification = builder
                    ?.setContentTitle("Charging")
                    ?.setContentText("Battery Percentage : "+batteryLevel+"%")
                    ?.setAutoCancel(false)
                    ?.setOngoing(true)
                    ?.setSmallIcon(R.drawable.ic_battery_charging_50_black_24dp)
                    ?.build()

                val manager = context?.getSystemService(NotificationManager::class.java)
                manager?.notify(NOTIFICATION_ID,notification)

            }else if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){
                notification = builder
                    ?.setContentTitle("Not Charging")
                    ?.setContentText("Battery Percentage : "+batteryLevel+"%")
                    ?.setSmallIcon(R.drawable.ic_battery_50_black_24dp)
                    ?.build()


                val manager = context?.getSystemService(NotificationManager::class.java)
                manager?.notify(NOTIFICATION_ID,notification)

            }





//            Toast.makeText(context,"This is differenrt 2nd broadcast receiver",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()

        this.registerReceiver(changeBatteryBroadcast,intentFilter)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        createNotificationChannel()


        var notification =NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Charge Status")
            .setContentText("Current charge status")
            .setSmallIcon(R.drawable.ic_battery_50_black_24dp)
            .build()

        startForeground(NOTIFICATION_ID,notification)



        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(changeBatteryBroadcast)
    }

    private fun createNotificationChannel() {


        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            var serviceChannel = NotificationChannel(CHANNEL_ID,"Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)

            var manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(serviceChannel)

        }


    }
}
