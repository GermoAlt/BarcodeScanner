package com.assa.barcodereader.activity.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.assa.barcodereader.R
import com.assa.barcodereader.activity.main.MainActivity
import com.assa.barcodereader.task.DatabasePrepopulationTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val executor: ExecutorService = Executors.newCachedThreadPool()
        executor.submit(DatabasePrepopulationTask(applicationContext))

        findViewById<Button>(R.id.button_comenzar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}