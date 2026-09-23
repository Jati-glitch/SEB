package com.example.data

import kotlinx.coroutines.flow.Flow

class ExamRepository(private val examDao: ExamDao) {
    val allViolations: Flow<List<ViolationLog>> = examDao.getAllViolations()
    val allSessions: Flow<List<ExamSession>> = examDao.getAllSessions()

    suspend fun logViolation(violation: ViolationLog): Long {
        return examDao.insertViolation(violation)
    }

    suspend fun clearViolations() {
        examDao.clearAllViolations()
    }

    suspend fun startSession(session: ExamSession): Long {
        return examDao.insertSession(session)
    }

    suspend fun updateSession(session: ExamSession) {
        examDao.updateSession(session)
    }

    suspend fun getSession(id: Long): ExamSession? {
        return examDao.getSessionById(id)
    }

    suspend fun clearSessions() {
        examDao.clearAllSessions()
    }
}
