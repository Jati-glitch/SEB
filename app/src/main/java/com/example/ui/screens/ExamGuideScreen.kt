package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.EmeraldSuccess

@Composable
fun ExamGuideScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = CyberBluePrimary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Panduan Safe Exam Browser (SEB)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Petunjuk pelaksanaan ujian aman berbasis Android",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Guide Item 1
        GuideSectionCard(
            icon = Icons.Default.Security,
            title = "1. Cara Kerja Safe Exam Browser",
            body = "Safe Exam Browser mengubah perangkat HP/Tablet Android menjadi workstation ujian terkunci. Aplikasi memblokir akses ke web eksternal, melarang navigasi keluar aplikasi, dan menonaktifkan fitur tangkapan layar serta perekam layar."
        )

        // Guide Item 2
        GuideSectionCard(
            icon = Icons.Default.Lock,
            title = "2. Deteksi Pelanggaran (Anti-Cheat Strikes)",
            body = "Jika peserta mencoba membuka notifikasi, floating window, split-screen, menekan tombol Home/Recent, atau meminimalkan aplikasi, sistem otomatis menghitung 1 pelanggaran (Strike). Jika mencapai batas maksimal (default 3x), ujian akan dibekukan total dan harus dibuka oleh pengawas."
        )

        // Guide Item 3
        GuideSectionCard(
            icon = Icons.Default.PhoneAndroid,
            title = "3. Penguncian Layar (Screen Pinning / Kiosk)",
            body = "Sistem Android mendukung penguncian aplikasi (Screen Pinning). Saat ujian aktif, bilah navigasi dan status bar disembunyikan. Pada perangkat sekolah/lab, aplikasi ini dapat dijadikan Device Owner / Lock Task untuk mencegah tombol fisik digunakan."
        )

        // Guide Item 4
        GuideSectionCard(
            icon = Icons.Default.Key,
            title = "4. Peran Pengawas & PIN Otorisasi",
            body = "PIN Pengawas default adalah '1234' (dapat diubah di tab Pengaturan). Hanya pengawas yang memegang PIN ini. Siswa tidak dapat mengakhiri sesi ujian sebelum waktu berakhir tanpa konfirmasi PIN dari pengawas."
        )

        // Guide Item 5: Pemisahan Akses Siswa & Panitia
        GuideSectionCard(
            icon = Icons.Default.Lock,
            title = "5. Pemisahan Hak Akses: Siswa vs Panitia",
            body = "• Mode Siswa: Siswa hanya dapat mengisi nama/NISN/token dan memulai ujian. Siswa dilarang mengedit URL server ujian, tidak bisa membuka tab Pengaturan Keamanan, dan tidak dapat melihat log pelanggaran.\n• Mode Panitia: Dilindungi PIN khusus. Panitia/Guru dapat mengonfigurasi URL CBT sekolah, mengubah toleransi strike kecurangan, mengatur proteksi screenshot/kiosk, serta melihat dan mereset riwayat pelanggaran."
        )

        // Guide Item 6: Kompatibilitas CBT
        GuideSectionCard(
            icon = Icons.Default.Info,
            title = "6. Kompatibilitas Server CBT Sekolah",
            body = "Aplikasi mendukung berbagai platform ujian online dan server lokal sekolah:\n• Google Forms CBT\n• Moodle LMS Exam\n• Candy CBT / FlyExam / Madrasah CBT\n• IP Server Lokal (misal: 192.168.1.100/cbt)\n• Web kuis mandiri berbasis HTML5/Javascript."
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GuideSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
