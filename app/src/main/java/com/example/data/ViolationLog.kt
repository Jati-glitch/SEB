package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "violation_logs")
data class ViolationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val examTitle: String,
    val studentName: String,
    val reason: String,
    val strikeNumber: Int,
    val maxStrikes: Int
)
