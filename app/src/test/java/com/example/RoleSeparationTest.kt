package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.AppScreen
import com.example.ui.ExamViewModel
import com.example.ui.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoleSeparationTest {

    private lateinit var viewModel: ExamViewModel

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = ExamViewModel(app)
    }

    @Test
    fun defaultRoleIsStudent() {
        assertEquals(UserRole.STUDENT, viewModel.uiState.value.currentRole)
    }

    @Test
    fun studentCannotDirectlyNavigateToSettings() {
        assertEquals(AppScreen.SETUP, viewModel.uiState.value.currentScreen)
        // Attempt to navigate to Settings as student
        viewModel.navigateTo(AppScreen.SETTINGS)

        // Screen should still be SETUP and login dialog should trigger
        assertEquals(AppScreen.SETUP, viewModel.uiState.value.currentScreen)
        assertTrue(viewModel.uiState.value.showRoleLoginDialog)
    }

    @Test
    fun studentCannotDirectlyNavigateToLogs() {
        assertEquals(AppScreen.SETUP, viewModel.uiState.value.currentScreen)
        // Attempt to navigate to Logs as student
        viewModel.navigateTo(AppScreen.LOGS)

        // Screen should still be SETUP and login dialog should trigger
        assertEquals(AppScreen.SETUP, viewModel.uiState.value.currentScreen)
        assertTrue(viewModel.uiState.value.showRoleLoginDialog)
    }

    @Test
    fun studentCanNavigateToGuide() {
        viewModel.navigateTo(AppScreen.GUIDE)
        assertEquals(AppScreen.GUIDE, viewModel.uiState.value.currentScreen)
        assertFalse(viewModel.uiState.value.showRoleLoginDialog)
    }

    @Test
    fun proctorLoginWithWrongPinFails() {
        val success = viewModel.loginAsProctor("0000")
        assertFalse(success)
        assertEquals(UserRole.STUDENT, viewModel.uiState.value.currentRole)
    }

    @Test
    fun proctorLoginWithCorrectPinSucceedsAndCanAccessSettings() {
        val success = viewModel.loginAsProctor("1234")
        assertTrue(success)
        assertEquals(UserRole.PROCTOR, viewModel.uiState.value.currentRole)
        assertFalse(viewModel.uiState.value.showRoleLoginDialog)

        // Now proctor can navigate to Settings
        viewModel.navigateTo(AppScreen.SETTINGS)
        assertEquals(AppScreen.SETTINGS, viewModel.uiState.value.currentScreen)

        // And proctor can navigate to Logs
        viewModel.navigateTo(AppScreen.LOGS)
        assertEquals(AppScreen.LOGS, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun switchingBackToStudentRevokesSettingsScreen() {
        // Log in as proctor and go to settings
        viewModel.loginAsProctor("1234")
        viewModel.navigateTo(AppScreen.SETTINGS)
        assertEquals(AppScreen.SETTINGS, viewModel.uiState.value.currentScreen)

        // Switch to student
        viewModel.switchToStudent()
        assertEquals(UserRole.STUDENT, viewModel.uiState.value.currentRole)
        // Screen automatically drops back to SETUP to prevent student viewing settings
        assertEquals(AppScreen.SETUP, viewModel.uiState.value.currentScreen)
    }
}
