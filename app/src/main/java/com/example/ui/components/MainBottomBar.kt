package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.UserRole

@Composable
fun MainBottomBar(
    currentScreen: AppScreen,
    currentRole: UserRole,
    onTabSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.SETUP,
            onClick = { onTabSelected(AppScreen.SETUP) },
            icon = {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = stringResource(R.string.tab_exam),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(R.string.tab_exam)) },
            modifier = Modifier.testTag("tab_setup"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.LOGS,
            onClick = { onTabSelected(AppScreen.LOGS) },
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = stringResource(R.string.tab_history),
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRole == UserRole.STUDENT) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 3.dp, y = 3.dp)
                        )
                    }
                }
            },
            label = { Text(stringResource(R.string.tab_history)) },
            modifier = Modifier.testTag("tab_logs"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS,
            onClick = { onTabSelected(AppScreen.SETTINGS) },
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.tab_settings),
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRole == UserRole.STUDENT) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 3.dp, y = 3.dp)
                        )
                    }
                }
            },
            label = { Text(stringResource(R.string.tab_settings)) },
            modifier = Modifier.testTag("tab_settings"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.GUIDE,
            onClick = { onTabSelected(AppScreen.GUIDE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = stringResource(R.string.tab_guide),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(R.string.tab_guide)) },
            modifier = Modifier.testTag("tab_guide"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
