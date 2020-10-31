package com.mindfulai.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mindfulai.Models.Notification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification")
    fun getAllNotifications(): List<Notification>
    @Insert
    fun saveNotification(notification: Notification)
    @Query("DELETE FROM notification")
    fun deleteAllNotifications()
}