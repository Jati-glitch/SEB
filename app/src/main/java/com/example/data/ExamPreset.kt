package com.example.data

data class ExamPreset(
    val id: String,
    val name: String,
    val category: String,
    val url: String,
    val description: String,
    val requiresToken: Boolean = false
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            ExamPreset(
                id = "demo_cbt",
                name = "Demo Simulasi CBT",
                category = "Simulasi",
                url = "https://html5test.opensuse.org",
                description = "Uji coba browser aman dengan halaman tes interaktif"
            ),
            ExamPreset(
                id = "google_forms",
                name = "Google Forms CBT",
                category = "Kuis Daring",
                url = "https://docs.google.com/forms",
                description = "Ujian berbasis kuis dan formulir Google Forms",
                requiresToken = true
            ),
            ExamPreset(
                id = "candy_cbt",
                name = "Candy CBT / Madrasah",
                category = "CBT Sekolah",
                url = "http://192.168.0.200/cbt",
                description = "Server lokal CBT standar UNBK / Madrasah"
            ),
            ExamPreset(
                id = "moodle_lms",
                name = "Moodle LMS Exam",
                category = "LMS",
                url = "https://moodle.org/demo",
                description = "Kuis online Moodle dengan integrasi SEB Safe Exam"
            )
        )
    }
}
