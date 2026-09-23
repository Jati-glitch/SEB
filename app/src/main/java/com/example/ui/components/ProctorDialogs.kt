package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberNotice
import com.example.ui.theme.CrimsonWarning

@Composable
fun ViolationWarningDialog(
    reason: String,
    strikes: Int,
    maxStrikes: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AmberNotice.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Peringatan",
                    tint = AmberNotice,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Peringatan Pelanggaran!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Aktivitas mencurigakan terdeteksi: $reason",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status Pelanggaran",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$strikes dari $maxStrikes kali",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonWarning
                        )
                    }
                }

                Text(
                    text = "Perhatian: Jika mencapai $maxStrikes kali pelanggaran, ujian akan otomatis terkunci permanen dan harus dibuka oleh pengawas!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_warning_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Lanjutkan Ujian")
            }
        }
    )
}

@Composable
fun ExamLockedDialog(
    strikes: Int,
    maxStrikes: Int,
    onUnlockWithPin: (String) -> Boolean,
    onAbortExam: (String) -> Boolean
) {
    var enteredPin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { /* Cannot dismiss by tapping outside */ },
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CrimsonWarning.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Terkunci",
                    tint = CrimsonWarning,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(
                text = "Ujian Terkunci!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CrimsonWarning,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Batas pelanggaran telah tercapai ($strikes/$maxStrikes). Siswa dilarang melanjutkan ujian sampai pengawas memasukkan PIN otorisasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = {
                        enteredPin = it
                        isPinError = false
                    },
                    label = { Text("PIN Pengawas (Default: 1234)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!onUnlockWithPin(enteredPin)) {
                                isPinError = true
                            }
                        }
                    ),
                    isError = isPinError,
                    supportingText = {
                        if (isPinError) {
                            Text("PIN pengawas salah!", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locked_proctor_pin_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!onUnlockWithPin(enteredPin)) {
                        isPinError = true
                    }
                },
                modifier = Modifier.testTag("unlock_exam_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Buka Sesi Ujian")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    if (!onAbortExam(enteredPin)) {
                        isPinError = true
                    }
                },
                modifier = Modifier.testTag("abort_exam_button")
            ) {
                Text("Akhiri Sesi Ujian")
            }
        }
    )
}

@Composable
fun ProctorExitDialog(
    onDismiss: () -> Unit,
    onConfirmExit: (String) -> Boolean
) {
    var enteredPin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Otorisasi Pengawas",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Otorisasi Pengawas Ujian",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Untuk keluar dari Safe Exam Browser sebelum ujian berakhir, masukkan PIN Pengawas.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = {
                        enteredPin = it
                        isPinError = false
                    },
                    label = { Text("PIN Pengawas (Default: 1234)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!onConfirmExit(enteredPin)) {
                                isPinError = true
                            }
                        }
                    ),
                    isError = isPinError,
                    supportingText = {
                        if (isPinError) {
                            Text("PIN tidak sesuai!", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("proctor_exit_pin_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!onConfirmExit(enteredPin)) {
                        isPinError = true
                    }
                },
                modifier = Modifier.testTag("confirm_exit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Keluar Ujian")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_exit_button")
            ) {
                Text("Kembali ke Ujian")
            }
        }
    )
}
