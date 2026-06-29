package com.rhythmwave.gymflow.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.rhythmwave.gymflow.MainActivity
import com.rhythmwave.gymflow.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutTimerService : Service() {

    private val binder = LocalBinder()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutTimerService = this@WorkoutTimerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val seconds = intent.getIntExtra(EXTRA_SECONDS, 60)
                startTimer(seconds)
            }
            ACTION_STOP -> stopTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_ADD_TIME -> addTime(30)
        }
        return START_STICKY
    }

    fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        _timerState.value = TimerState(
            isRunning = true,
            isPaused = false,
            totalSeconds = durationSeconds,
            remainingSeconds = durationSeconds
        )
        startForeground(NOTIFICATION_ID, createNotification(durationSeconds))
        startCountdown(durationSeconds)
    }

    private fun startCountdown(fromSeconds: Int) {
        timerJob = scope.launch {
            var remaining = fromSeconds
            while (remaining > 0 && _timerState.value.isRunning) {
                if (!_timerState.value.isPaused) {
                    _timerState.value = _timerState.value.copy(remainingSeconds = remaining)
                    updateNotification(remaining)
                    delay(1000)
                    remaining--
                } else {
                    delay(100)
                }
            }
            if (remaining <= 0) {
                onTimerComplete()
            }
        }
    }

    private fun onTimerComplete() {
        _timerState.value = TimerState(isComplete = true)
        vibrate()
        updateNotification(0, isComplete = true)
        // Auto-stop after 5 seconds
        scope.launch {
            delay(5000)
            if (_timerState.value.isComplete) {
                stopTimer()
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isPaused = true)
    }

    fun resumeTimer() {
        _timerState.value = _timerState.value.copy(isPaused = false)
    }

    fun addTime(seconds: Int) {
        val current = _timerState.value
        _timerState.value = current.copy(
            totalSeconds = current.totalSeconds + seconds,
            remainingSeconds = current.remainingSeconds + seconds
        )
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 200, 100, 200, 100, 300)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun createNotification(seconds: Int): Notification {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rest Timer")
            .setContentText(formatTime(seconds))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(android.R.drawable.ic_media_pause, "Pause",
                createActionIntent(ACTION_PAUSE))
            .addAction(android.R.drawable.ic_media_ff, "+30s",
                createActionIntent(ACTION_ADD_TIME))
            .addAction(android.R.drawable.ic_delete, "Stop",
                createActionIntent(ACTION_STOP))
            .build()
    }

    private fun updateNotification(seconds: Int, isComplete: Boolean = false) {
        val notification = if (isComplete) {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Rest Complete!")
                .setContentText("Time to start your next set")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(false)
                .setAutoCancel(true)
                .build()
        } else {
            createNotification(seconds)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(this, WorkoutTimerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Workout Timer",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows rest timer during workouts"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "workout_timer"
        const val ACTION_START = "com.rhythmwave.gymflow.START"
        const val ACTION_STOP = "com.rhythmwave.gymflow.STOP"
        const val ACTION_PAUSE = "com.rhythmwave.gymflow.PAUSE"
        const val ACTION_RESUME = "com.rhythmwave.gymflow.RESUME"
        const val ACTION_ADD_TIME = "com.rhythmwave.gymflow.ADD_TIME"
        const val EXTRA_SECONDS = "seconds"
    }
}

data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0
) {
    val progress: Float
        get() = if (totalSeconds > 0) {
            1f - (remainingSeconds.toFloat() / totalSeconds)
        } else 0f

    val displayTime: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return "%d:%02d".format(mins, secs)
        }
}
