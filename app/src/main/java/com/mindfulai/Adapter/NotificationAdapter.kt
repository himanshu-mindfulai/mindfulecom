package com.mindfulai.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindfulai.Models.Notification
import com.mindfulai.ministore.databinding.NotificationItemLayoutBinding
import java.util.*

class NotificationAdapter(val context: Context, val data: List<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: NotificationItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(notification: Notification) {
            binding.title.text = notification.title
            binding.body.text = notification.body
            var duration = Date().time - Date(notification.time).time
            binding.time.text = convertDurationToReadableString(duration)
        }

        fun convertDurationToReadableString(duration: Long) :String{
            var seconds = (duration/1000)
            if (seconds < 60){
                return "$seconds secs ago"
            }
            var minutes = seconds/60
            if (minutes < 60){
                return "$minutes mins ago"
            }
            var hours = minutes/60
            if (hours < 24){
                return "$hours hours ago"
            }
            var days = hours/24
            return "$days days ago"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(data[position])
}