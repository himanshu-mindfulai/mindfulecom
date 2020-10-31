package com.mindfulai.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "body")
        var body: String,
        @ColumnInfo(name = "image")
        var image: String = "",
        @ColumnInfo(name = "time")
        var time: String
)