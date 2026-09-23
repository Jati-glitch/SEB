package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.security.SecurityManager
import com.example.ui.AppScreen
import com.example.ui.ExamViewModel
import com.example.ui.UserRole
import com.example.ui.components.ExamLockedDialog
import com.example.ui.components.MainBottomBar
import com.example.ui.components.ProctorExitDialog
import com.example.ui.components.ProctorLoginDialog
import com.example.ui.components.ViolationWarningDialog
import com.example.ui.screens.ActiveExamScreen
import com.example.ui.screens.ExamGuideScreen
import com.example.ui.screens.PreExamSetupScreen
import com.example.ui.screens.SecuritySettingsScreen
import com.example.ui.screens.StudentSimpleExamScreen
import com.example.ui.screens.ViolationLogsScreen
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SafeExamApp(
                    activity = this,
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val state = viewModel.uiState.value
        if (state.currentScreen == AppScreen.ACTIVE_EXAM) {
            if (hasFocus) {
                // Re-apply immersive mode when focus regained
                SecurityManager.setImmersiveMode(this, true)
            } else {
                // Focus lost: e.g. floating window, status bar pulled, or split-screen
                if (!state.showProctorExitDialog && !state.showProctorUnlockDialog && !state.showWarningDialog) {
                    viewModel.recordViolation(
                        activity = this,
                        reason = "Jendela kehilangan fokus (Notifikasi / Split-Screen / Floating App)"
                    )
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val state = viewModel.uiState.value
        if (state.currentScreen == AppScreen.ACTIVE_EXAM) {
            viewModel.recordViolation(
                activity = this,
                reason = "Upaya meninggalkan aplikasi (Tombol Home / Recent Apps)"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeExamApp(
    activity: MainActivity,
    viewModel: ExamViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.currentScreen == AppScreen.ACTIVE_EXAM) {
        // Fullscreen Active Exam Mode
        Box(modifier = Modifier.fillMaxSize().testTag("active_exam_container")) {
            ActiveExamScreen(
                state = state,
                viewModel = viewModel
            )

            // Warning dialog for strikes
            if (state.showWarningDialog) {
                ViolationWarningDialog(
                    reason = state.lastViolationReason,
                    strikes = state.strikes,
                    maxStrikes = state.maxStrikes,
                    onDismiss = { viewModel.dismissWarning() }
                )
            }

            // Locked dialog when max strikes reached
            if (state.showProctorUnlockDialog) {
                ExamLockedDialog(
                    strikes = state.strikes,
                    maxStrikes = state.maxStrikes,
                    onUnlockWithPin = { pin ->
                        viewModel.unlockByProctor(pin)
                    },
                    onAbortExam = { pin ->
                        viewModel.finishExam(activity, enteredPin = pin, requirePin = true)
                    }
                )
            }

            // Proctor exit dialog when student taps exit
            if (state.showProctorExitDialog) {
                ProctorExitDialog(
                    onDismiss = { viewModel.dismissProctorExit() },
                    onConfirmExit = { pin ->
                        viewModel.finishExam(activity, enteredPin = pin, requirePin = true)
                    }
                )
            }
        }
    } else {
        val isStudent = state.currentRole == UserRole.STUDENT

        // Management / Configuration Mode with Scaffold
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isStudent) {
                                    stringResource(R.string.app_name)
                                } else {
                                    when (state.currentScreen) {
                                        AppScreen.SETUP -> "Panel Panitia CBT"
                                        AppScreen.LOGS -> stringResource(R.string.tab_history)
                                        AppScreen.SETTINGS -> stringResource(R.string.tab_settings)
                                        AppScreen.GUIDE -> stringResource(R.string.tab_guide)
                                        else -> stringResource(R.string.app_name)
                                    }
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        // Role Status Pill & Switch Button
                        if (isStudent) {
                            OutlinedButton(
                                onClick = { viewModel.promptProctorLogin() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .testTag("topbar_login_proctor_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Guru / Panitia", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.switchToStudent()
                                    Toast.makeText(activity, "Beralih ke Mode Siswa (Sederhana)", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .testTag("topbar_switch_to_student_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mode Panitia", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                // In student mode, bottom navigation bar is hidden for extreme simplicity and zero distractions
                if (!isStudent) {
                    MainBottomBar(
                        currentScreen = state.currentScreen,
                        currentRole = state.currentRole,
                        onTabSelected = { newScreen ->
                            viewModel.navigateTo(newScreen)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isStudent) {
                    // Simple Student Screen: Only name, student ID, token, server status, and start button
                    StudentSimpleExamScreen(state = state, viewModel = viewModel)
                } else {
                    // Full Proctor / Panitia View with Tabs
                    when (state.currentScreen) {
                        AppScreen.SETUP -> PreExamSetupScreen(state = state, viewModel = viewModel)
                        AppScreen.LOGS -> ViolationLogsScreen(viewModel = viewModel)
                        AppScreen.SETTINGS -> SecuritySettingsScreen(state = state, viewModel = viewModel)
                        AppScreen.GUIDE -> ExamGuideScreen()
                        AppScreen.ACTIVE_EXAM -> Unit
                    }
                }
            }

            // Proctor Login Modal Dialog
            if (state.showRoleLoginDialog) {
                ProctorLoginDialog(
                    onDismiss = { viewModel.dismissRoleLoginDialog() },
                    onLoginSuccess = { pin ->
                        val success = viewModel.loginAsProctor(pin)
                        if (success) {
                            Toast.makeText(activity, "Berhasil masuk sebagai Panitia!", Toast.LENGTH_SHORT).show()
                        }
                        success
                    }
                )
            }
        }
    }
}
