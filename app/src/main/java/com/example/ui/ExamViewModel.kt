package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExamPreset
import com.example.data.ExamRepository
import com.example.data.ExamSession
import com.example.data.ViolationLog
import com.example.security.SecurityManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UserRole {
    STUDENT,
    PROCTOR // Panitia / Guru / Administrator
}

enum class AppScreen {
    SETUP,
    ACTIVE_EXAM,
    LOGS,
    SETTINGS,
    GUIDE
}

data class ExamUiState(
    val currentScreen: AppScreen = AppScreen.SETUP,
    val currentRole: UserRole = UserRole.STUDENT,
    val showRoleLoginDialog: Boolean = false,
    val targetRoleForLogin: UserRole = UserRole.PROCTOR,
    val proctorPin: String = "1234",
    val examUrl: String = "https://html5test.opensuse.org",
    val studentName: String = "Peserta Ujian 01",
    val studentId: String = "NISN-202609001",
    val examToken: String = "",
    val activeSessionId: Long? = null,
    val strikes: Int = 0,
    val maxStrikes: Int = 3,
    val isLocked: Boolean = false,
    val showWarningDialog: Boolean = false,
    val lastViolationReason: String = "",
    val showProctorExitDialog: Boolean = false,
    val showProctorUnlockDialog: Boolean = false,
    val flagSecureEnabled: Boolean = true,
    val kioskModeEnabled: Boolean = true,
    val clearClipboardEnabled: Boolean = true,
    val antiCheatDetectionEnabled: Boolean = true,
    val customUserAgent: String = SecurityManager.DEFAULT_USER_AGENT,
    val elapsedSeconds: Long = 0L,
    val webViewLoadingProgress: Int = 0,
    val currentLoadedUrl: String = "",
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isWebLoading: Boolean = false,
    val selectedPresetId: String? = "demo_cbt"
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExamRepository
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    val violationLogs: StateFlow<List<ViolationLog>>
    val examSessions: StateFlow<List<ExamSession>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ExamRepository(database.examDao())

        violationLogs = repository.allViolations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        examSessions = repository.allSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: AppScreen) {
        // Cannot leave active exam without proctor PIN
        if (_uiState.value.currentScreen == AppScreen.ACTIVE_EXAM && screen != AppScreen.ACTIVE_EXAM) {
            _uiState.update { it.copy(showProctorExitDialog = true) }
            return
        }

        // Student cannot access Settings or Violation Logs without Proctor login
        if (_uiState.value.currentRole == UserRole.STUDENT && (screen == AppScreen.SETTINGS || screen == AppScreen.LOGS)) {
            _uiState.update {
                it.copy(
                    showRoleLoginDialog = true,
                    targetRoleForLogin = UserRole.PROCTOR
                )
            }
            return
        }

        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun promptProctorLogin() {
        _uiState.update {
            it.copy(
                showRoleLoginDialog = true,
                targetRoleForLogin = UserRole.PROCTOR
            )
        }
    }

    fun dismissRoleLoginDialog() {
        _uiState.update { it.copy(showRoleLoginDialog = false) }
    }

    fun loginAsProctor(pin: String): Boolean {
        if (verifyProctorPin(pin)) {
            _uiState.update {
                it.copy(
                    currentRole = UserRole.PROCTOR,
                    showRoleLoginDialog = false
                )
            }
            return true
        }
        return false
    }

    fun switchToStudent() {
        _uiState.update {
            it.copy(
                currentRole = UserRole.STUDENT,
                // If currently on proctor-only screen, redirect to SETUP
                currentScreen = if (it.currentScreen == AppScreen.SETTINGS || it.currentScreen == AppScreen.LOGS) {
                    AppScreen.SETUP
                } else {
                    it.currentScreen
                }
            )
        }
    }

    fun setExamUrl(url: String) {
        _uiState.update { it.copy(examUrl = url, selectedPresetId = null) }
    }

    fun setStudentName(name: String) {
        _uiState.update { it.copy(studentName = name) }
    }

    fun setStudentId(id: String) {
        _uiState.update { it.copy(studentId = id) }
    }

    fun setExamToken(token: String) {
        _uiState.update { it.copy(examToken = token) }
    }

    fun selectPreset(preset: ExamPreset) {
        _uiState.update {
            it.copy(
                examUrl = preset.url,
                selectedPresetId = preset.id
            )
        }
    }

    fun startExam(activity: Activity) {
        val state = _uiState.value
        val formattedUrl = when {
            state.examUrl.startsWith("http://") || state.examUrl.startsWith("https://") -> state.examUrl
            else -> "https://${state.examUrl.trim()}"
        }

        // Apply hardware and security settings
        if (state.flagSecureEnabled) {
            SecurityManager.setScreenshotProtection(activity, true)
        }
        SecurityManager.setKeepScreenOn(activity, true)
        SecurityManager.setImmersiveMode(activity, true)

        if (state.kioskModeEnabled) {
            SecurityManager.startKioskMode(activity)
        }

        if (state.clearClipboardEnabled) {
            SecurityManager.clearClipboard(activity)
        }

        viewModelScope.launch {
            val sessionId = repository.startSession(
                ExamSession(
                    title = if (state.selectedPresetId != null) "Ujian (${state.selectedPresetId})" else "Ujian Daring",
                    studentName = state.studentName.ifBlank { "Peserta" },
                    studentId = state.studentId.ifBlank { "NISN" },
                    examUrl = formattedUrl,
                    status = "ACTIVE"
                )
            )

            _uiState.update {
                it.copy(
                    examUrl = formattedUrl,
                    activeSessionId = sessionId,
                    strikes = 0,
                    isLocked = false,
                    showWarningDialog = false,
                    elapsedSeconds = 0L,
                    currentScreen = AppScreen.ACTIVE_EXAM
                )
            }

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun recordViolation(activity: Activity, reason: String) {
        val state = _uiState.value
        if (state.currentScreen != AppScreen.ACTIVE_EXAM || !state.antiCheatDetectionEnabled) return

        SecurityManager.triggerVibrationWarning(activity)

        val newStrikes = state.strikes + 1
        val shouldLock = newStrikes >= state.maxStrikes

        viewModelScope.launch {
            repository.logViolation(
                ViolationLog(
                    examTitle = "Sesi #${state.activeSessionId ?: 0}",
                    studentName = state.studentName,
                    reason = reason,
                    strikeNumber = newStrikes,
                    maxStrikes = state.maxStrikes
                )
            )

            state.activeSessionId?.let { sId ->
                val session = repository.getSession(sId)
                if (session != null) {
                    repository.updateSession(
                        session.copy(
                            strikeCount = newStrikes,
                            status = if (shouldLock) "LOCKED" else session.status
                        )
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                strikes = newStrikes,
                isLocked = shouldLock,
                lastViolationReason = reason,
                showWarningDialog = !shouldLock,
                showProctorUnlockDialog = shouldLock
            )
        }
    }

    fun dismissWarning() {
        _uiState.update { it.copy(showWarningDialog = false) }
    }

    fun promptProctorExit() {
        _uiState.update { it.copy(showProctorExitDialog = true) }
    }

    fun dismissProctorExit() {
        _uiState.update { it.copy(showProctorExitDialog = false) }
    }

    fun promptProctorUnlock() {
        _uiState.update { it.copy(showProctorUnlockDialog = true) }
    }

    fun verifyProctorPin(enteredPin: String): Boolean {
        return enteredPin.trim() == _uiState.value.proctorPin.trim()
    }

    fun unlockByProctor(enteredPin: String): Boolean {
        if (!verifyProctorPin(enteredPin)) return false

        _uiState.update {
            it.copy(
                isLocked = false,
                showProctorUnlockDialog = false,
                strikes = 0 // Reset strikes upon proctor authorization
            )
        }
        return true
    }

    fun finishExam(activity: Activity, enteredPin: String? = null, requirePin: Boolean = false): Boolean {
        if (requirePin) {
            if (enteredPin == null || !verifyProctorPin(enteredPin)) {
                return false
            }
        }

        timerJob?.cancel()

        // Release hardware restrictions
        SecurityManager.setScreenshotProtection(activity, false)
        SecurityManager.setKeepScreenOn(activity, false)
        SecurityManager.setImmersiveMode(activity, false)
        SecurityManager.stopKioskMode(activity)

        viewModelScope.launch {
            val state = _uiState.value
            state.activeSessionId?.let { sId ->
                val session = repository.getSession(sId)
                if (session != null) {
                    repository.updateSession(
                        session.copy(
                            endTime = System.currentTimeMillis(),
                            status = if (state.isLocked) "LOCKED" else "COMPLETED"
                        )
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.SETUP,
                showProctorExitDialog = false,
                showProctorUnlockDialog = false,
                isLocked = false,
                strikes = 0,
                activeSessionId = null
            )
        }
        return true
    }

    fun updateWebProgress(progress: Int) {
        _uiState.update {
            it.copy(
                webViewLoadingProgress = progress,
                isWebLoading = progress in 1..99
            )
        }
    }

    fun updateWebNavigation(canBack: Boolean, canForward: Boolean, url: String) {
        _uiState.update {
            it.copy(
                canGoBack = canBack,
                canGoForward = canForward,
                currentLoadedUrl = url
            )
        }
    }

    fun updateSecuritySettings(
        flagSecure: Boolean,
        kioskMode: Boolean,
        antiCheat: Boolean,
        clearClipboard: Boolean,
        maxStrikes: Int,
        newPin: String
    ) {
        _uiState.update {
            it.copy(
                flagSecureEnabled = flagSecure,
                kioskModeEnabled = kioskMode,
                antiCheatDetectionEnabled = antiCheat,
                clearClipboardEnabled = clearClipboard,
                maxStrikes = maxStrikes,
                proctorPin = newPin.ifBlank { "1234" }
            )
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearViolations()
            repository.clearSessions()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
