package com.example.testapplication.workmanager

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.testapplication.R

class WorkManagerTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work_manager_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val worker: OneTimeWorkRequest = generateWorkRequest()
        val btnRun: Button = findViewById(R.id.btn_run)
        btnRun.setOnClickListener {
            WorkManager.getInstance(this)
                .enqueueUniqueWork(
                    "test",
                    ExistingWorkPolicy.REPLACE,
                    worker
                )
        }

        val btnCancel: Button = findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            WorkManager.getInstance(this).cancelWorkById(worker.id)
        }
    }

    private fun generateWorkRequest(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<TestWorker>()
            .build()
    }
}