package com.example.security

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object SecurityManager {

    const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36 SafeExamBrowser/3.0-SEB-Android"

    fun setScreenshotProtection(activity: Activity, enabled: Boolean) {
        try {
            if (enabled) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setKeepScreenOn(activity: Activity, enabled: Boolean) {
        try {
            if (enabled) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setImmersiveMode(activity: Activity, enabled: Boolean) {
        try {
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            if (enabled) {
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startKioskMode(activity: Activity): Boolean {
        return try {
            activity.startLockTask()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stopKioskMode(activity: Activity) {
        try {
            activity.stopLockTask()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearClipboard(context: Context) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    clipboard.clearPrimaryClip()
                } else {
                    val clip = ClipData.newPlainText("", "")
                    clipboard.setPrimaryClip(clip)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerVibrationWarning(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(300)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
