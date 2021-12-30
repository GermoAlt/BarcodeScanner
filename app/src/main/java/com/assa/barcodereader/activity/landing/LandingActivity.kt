package com.assa.barcodereader.activity.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.assa.barcodereader.R
import com.assa.barcodereader.activity.main.MainActivity

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        findViewById<Button>(R.id.button_comenzar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}