package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_sessions")
data class ExamSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val studentName: String,
    val studentId: String,
    val examUrl: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, LOCKED, ABORTED
    val strikeCount: Int = 0
)
