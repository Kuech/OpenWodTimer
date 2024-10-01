package io.github.openwodtimer.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import io.github.openwodtimer.R
import io.github.openwodtimer.ui.WorkoutType
import io.github.openwodtimer.ui.WodTimerParamName
import io.github.openwodtimer.utils.TimerServiceManager
import io.github.openwodtimer.wodTimer.IWodTimer
import io.github.openwodtimer.wodTimer.MultiRoundWodTimer
import io.github.openwodtimer.wodTimer.WodTimer
import io.github.openwodtimer.wodTimer.WorkoutState

class WodTimerService : Service()
{
    var wodTimer: IWodTimer? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setWodTimer(intent)
        TimerServiceManager.registerService(this)
        startForegroundService()
        startAndListenWodTimerEvent()
        wodTimer?.start()

        return START_STICKY
    }
    override fun onDestroy() {
        TimerServiceManager.unregisterService()
        super.onDestroy()
    }

    private fun setWodTimer(intent: Intent?){
        val workTime = intent?.getLongExtra(WodTimerParamName.WORKTIME.name, 0L) ?: 0L
        val workoutTypeValue = intent?.getIntExtra(WodTimerParamName.WORKOUTTYPE.name, 0) ?: 0
        val workoutType = enumValues<WorkoutType>()[workoutTypeValue]

        if(workoutType== WorkoutType.AMRAP || workoutType== WorkoutType.FORTIME){
            wodTimer = WodTimer(workCountDownTime = workTime)
        }
        if(workoutType== WorkoutType.EMOM || workoutType== WorkoutType.TABATA){
            val restTime = intent?.getLongExtra(WodTimerParamName.RESTTIME.name, 0L) ?: 0L
            val rounds = intent?.getIntExtra(WodTimerParamName.ROUNDS.name, 0) ?: 0
            wodTimer = MultiRoundWodTimer(workCountDownTime = workTime, restCountDownTime = restTime, rounds = rounds)
        }
        TimerServiceManager.setViewModelWorkoutType(workoutType)

    }

    private fun startAndListenWodTimerEvent(){
        wodTimer?.setOnTickListener(object : IWodTimer.OnTickListener {
            override fun onTick(remainingTime:Long, elapsedTime:Long) {
                TimerServiceManager.updateWodTimerData(remainingTime, elapsedTime)
            }
        })
        wodTimer?.setOnFinishedListener(object : IWodTimer.OnFinishedListener {
            override fun onFinished() {
                TimerServiceManager.wodTimerOnFinished()
                stopSelf()
            }
        })
        wodTimer?.setOnPausedListener(object: IWodTimer.OnPausedListener{
            override fun onPaused(paused: Boolean) {
                TimerServiceManager.wodTimerOnPaused(paused)
            }
        })
        wodTimer?.setOnNewRoundListener(object: IWodTimer.OnNewRoundListener{
            override fun onNewRound(
                currentRound: Int,
                currentWorkoutState: WorkoutState,
                setTime: Long
            ) {
                TimerServiceManager.wodTimerOnNewRound(currentRound, currentWorkoutState, setTime)
            }
        })
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("TimerServiceChannel", "Timer Service", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "TimerServiceChannel")
            .setContentTitle("Timer Running")
            .setContentText("Your timer is counting down.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(this, 100,notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        }
    }
}