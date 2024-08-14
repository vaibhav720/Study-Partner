package com.example.studytimer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageView: ImageView = findViewById(R.id.backgroundImageView)
        Glide.with(this)
            .load(R.drawable.background)
            .centerCrop()
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .into(imageView)
        val startButton: Button = findViewById(R.id.startButton)
        val timeInput: EditText = findViewById(R.id.timeInput)

        startButton.setOnClickListener {
            val time = timeInput.text.toString()
            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("TIMER_TIME", time)
            startActivity(intent)
        }
    }
}