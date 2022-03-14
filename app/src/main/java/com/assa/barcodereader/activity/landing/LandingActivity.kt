package com.assa.barcodereader.activity.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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
        if (!executor.submit(DatabasePrepopulationTask(applicationContext)).get()){
            Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.button_comenzar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}