package com.example.testapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.constraintlayout.ConstraintLayoutTestActivity
import com.example.testapplication.service.ServiceTestActivity
import com.example.testapplication.workmanager.WorkManagerTestActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTestActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = TestActivityAdapter() // Initialize adapter without the list
        recyclerView.adapter = adapter

        // Create your list of test activities
        val testActivities = listOf(
            TestActivityItem("WorkManagerTestActivity", WorkManagerTestActivity::class.java),
            TestActivityItem("ConstraintLayoutTestActivity", ConstraintLayoutTestActivity::class.java),
            TestActivityItem("ServiceTestActivity", ServiceTestActivity::class.java)
        )

        // Submit the list to the adapter
        adapter.submitList(testActivities)
    }
}