package com.example.testapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil // Import DiffUtil
import androidx.recyclerview.widget.ListAdapter // Import ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class TestActivityItem(
    val name: String,
    val activityClass: Class<out AppCompatActivity>
)

// Renamed to follow common naming conventions for DiffUtil.ItemCallback
object TestActivityItemDiffCallback : DiffUtil.ItemCallback<TestActivityItem>() {
    override fun areItemsTheSame(oldItem: TestActivityItem, newItem: TestActivityItem): Boolean {
        // If 'name' isn't guaranteed unique, consider adding a unique ID to TestActivityItem
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: TestActivityItem, newItem: TestActivityItem): Boolean {
        return oldItem == newItem // Data class 'equals' checks all properties
    }
}

class TestActivityAdapter :
    ListAdapter<TestActivityItem, TestActivityAdapter.ViewHolder>(TestActivityItemDiffCallback) { // Extend ListAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_test_item, parent, false) // Ensure this layout name is correct
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) // Use getItem() from ListAdapter
        holder.bind(item)
    }

    // No need for getItemCount() - ListAdapter handles this.
    // No need to store the list as a private val - ListAdapter handles this.

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityNameTextView: TextView = itemView.findViewById(R.id.textViewActivityName) // Ensure this ID is correct

        fun bind(item: TestActivityItem) {
            activityNameTextView.text = item.name
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, item.activityClass)
                context.startActivity(intent)
            }
        }
    }
}