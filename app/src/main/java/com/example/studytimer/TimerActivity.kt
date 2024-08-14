package com.example.studytimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.Locale




class TimerActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer
    private var timeInMillis: Long = 0
    private var timeRemainingInMillis: Long = 0 // Added this line
    private var isTimerRunning: Boolean = false // Added this line
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private val channelId = "study_timer_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer) // Replace with your actual layout name

        val imageView: ImageView = findViewById(R.id.backgroundImageView)
        Glide.with(this)
            .load(R.drawable.background)
            .centerCrop()
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .into(imageView)

        val timerTextView: TextView = findViewById(R.id.timerTextView)
        val pauseButton: Button = findViewById(R.id.pauseButton)
        val resumeButton: Button = findViewById(R.id.resumeButton) // Ensure you add this button to your layout
        val stopButton: Button = findViewById(R.id.stopButton)
        val playMusicButton: Button = findViewById(R.id.playMusicButton)
        val stopMusicButton: Button = findViewById(R.id.stopMusicButton)

        val time = intent.getStringExtra("TIMER_TIME")?.toLongOrNull() ?: 0
        timeInMillis = time * 60000 // Convert minutes to milliseconds

        mediaPlayer = MediaPlayer.create(this, R.raw.lofi_music) // Assuming you have a lofi_music.mp3 in raw

        startTimer(timerTextView, timeInMillis)

        pauseButton.setOnClickListener {
            if (isTimerRunning) {
                timer.cancel()
                isTimerRunning = false
            }
        }

        resumeButton.setOnClickListener {
            if (!isTimerRunning && timeRemainingInMillis > 0) {
                startTimer(timerTextView, timeRemainingInMillis)
            }
        }

        stopButton.setOnClickListener {
            timer.cancel()
            timerTextView.text = "00:00"
            isTimerRunning = false
        }

        playMusicButton.setOnClickListener {
            if (!isPlaying) {
                mediaPlayer.start()
                isPlaying = true
            }
        }

        stopMusicButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
            }
        }
    }

    private fun startTimer(timerTextView: TextView, timeInMillis: Long) {
        isTimerRunning = true
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished // Update the remaining time
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                sendNotification()
                startVibration()
                timerTextView.text = "00:00"
                isTimerRunning = false
                logTimer(timeInMillis)
            }
        }.start()
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Study Timer")
            .setContentText("Congratulations! Keep it up!")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Study Timer Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, builder.build())
    }

    private fun startVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(10000) // Vibrate for 10 seconds
    }

    private fun logTimer(timeInMillis: Long) {
        val sharedPref = getSharedPreferences("TimerLog", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentTime = System.currentTimeMillis()
        editor.putLong("Timer_$currentTime", timeInMillis)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
