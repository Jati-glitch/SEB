package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    // Violations
    @Query("SELECT * FROM violation_logs ORDER BY timestamp DESC")
    fun getAllViolations(): Flow<List<ViolationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: ViolationLog): Long

    @Query("DELETE FROM violation_logs")
    suspend fun clearAllViolations()

    // Sessions
    @Query("SELECT * FROM exam_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ExamSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ExamSession): Long

    @Update
    suspend fun updateSession(session: ExamSession)

    @Query("SELECT * FROM exam_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): ExamSession?

    @Query("DELETE FROM exam_sessions")
    suspend fun clearAllSessions()
}
