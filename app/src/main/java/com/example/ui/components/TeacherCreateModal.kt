package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCreateModal(
    currentUser: User,
    initialPost: Post? = null,
    onDismiss: () -> Unit,
    onSubmitMateri: (title: String, targetClass: String, structured: StructuredLearningContent) -> Unit,
    onSubmitTugas: (title: String, deskripsi: String, kelas: String, deadline: String, hashtag: String, attachmentName: String?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (initialPost?.type == PostType.ASSIGNMENT) 1 else 0) } // 0: Materi, 1: Tugas

    val classOptions = listOf("Semua Kelas", "VII-A", "VII-B", "VIII-B")

    // Materi Form State with Rich Media per section
    var materiJudul by remember { mutableStateOf(if (initialPost?.type == PostType.LEARNING) initialPost.title ?: "" else "Materi: Sistem Tata Surya & Fenomena Antariksa") }
    var materiKelas by remember { mutableStateOf(if (initialPost?.type == PostType.LEARNING) initialPost.targetClass ?: "Semua Kelas" else "Semua Kelas") }
    
    val defaultStructured = initialPost?.structuredContent
    
    var headerSection by remember {
        mutableStateOf(
            defaultStructured?.headerSection ?: SectionData(
                imageUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?auto=format&fit=crop&q=80&w=600"
            )
        )
    }

    var pemantikSection by remember {
        mutableStateOf(
            defaultStructured?.pemantik ?: SectionData(
                text = "Pernahkah kalian melihat bintang jatuh di langit malam? Sebenarnya apa itu bintang jatuh? Mengapa planet mengitari matahari tanpa bertabrakan?",
                imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&q=80&w=600"
            )
        )
    }

    var tujuanSection by remember {
        mutableStateOf(
            defaultStructured?.tujuan ?: SectionData(
                text = "Siswa dapat mengidentifikasi 8 komponen utama sistem tata surya, orbit planet, dan pengaruh gravitasi universal."
            )
        )
    }

    var intiSection by remember {
        mutableStateOf(
            defaultStructured?.inti ?: SectionData(
                text = "Perhatikan simulasi orbit planet berikut ini dan amati bagaimana gaya gravitasi menjaga keteraturan lintasan orbit.",
                embedUrl = "https://phet.colorado.edu/sims/html/gravity-and-orbits/latest/gravity-and-orbits_en.html",
                videoUrl = "https://www.youtube.com/watch?v=libKVRa01L8",
                pdfName = "Modul_Ajar_IPA_Sistem_Tata_Surya.pdf",
                fileName = "Simulasi PhET & Modul Ajar"
            )
        )
    }

    var asesmenSection by remember {
        mutableStateOf(
            defaultStructured?.asesmen ?: SectionData(
                text = "Tuliskan nama-nama planet berurutan dari yang terdekat dengan matahari dan selesaikan LKPD terlampir!",
                pdfName = "LKPD_Pengamatan_Fenomena_Antariksa.pdf"
            )
        )
    }

    // Tugas Form State (simplified initialization since editing assignment isn't fully requested yet, but just in case)
    var tugasJudul by remember { mutableStateOf(if (initialPost?.type == PostType.ASSIGNMENT) initialPost.title ?: "" else "Praktik Gerhana Bulan") }
    var tugasDeskripsi by remember { mutableStateOf(if (initialPost?.type == PostType.ASSIGNMENT) initialPost.content ?: "" else "Lakukan simulasi gerhana bulan menggunakan alat peraga sederhana di rumah (bola & senter). Foto hasilnya dan jelaskan zona umbra.") }
    var tugasKelas by remember { mutableStateOf(if (initialPost?.type == PostType.ASSIGNMENT) initialPost.targetClass ?: "VII-A" else "VII-A") }
    var tugasDeadline by remember { mutableStateOf(initialPost?.assignmentDetail?.deadline ?: "Besok, 23:59 WIB") }
    var tugasAttachmentName by remember { mutableStateOf(initialPost?.assignmentDetail?.attachmentName ?: "Panduan_Praktik_Gerhana_IPA7.pdf") }
    var showTugasPdfPreview by remember { mutableStateOf(false) }

    val cleanSubject = (currentUser.subject ?: "IPA").replace(" ", "").take(4)
    val generatedHashtag = "#Tugas${cleanSubject}${tugasKelas.replace("-", "")}${tugasJudul.replace(" ", "").take(7)}"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("teacher_create_modal_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (initialPost != null) {
                                if (selectedTab == 0) "Edit Modul & Materi" else "Edit Penugasan Terstruktur"
                            } else {
                                if (selectedTab == 0) "Buat Modul & Materi Pembelajaran" else "Buat Penugasan Terstruktur"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Guru Pengampu: ${currentUser.name} • Media & PDF Preview Aktif",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_teacher_modal")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = BilindiBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Modul Terstruktur", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Tugas & Asesmen", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (selectedTab == 0) {
                        // Materi Mode: Judul Section with Media Insertion (Gambar/Video/Embed/PDF)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(BilindiBlue)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📌 Judul Modul & Media Sampul",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = materiJudul,
                                    onValueChange = { materiJudul = it },
                                    label = { Text("Judul Modul / Materi Pelajaran") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Class selection for Materi
                                Text(
                                    text = "Target Kelas Materi:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    classOptions.forEach { cls ->
                                        FilterChip(
                                            selected = materiKelas == cls,
                                            onClick = { materiKelas = cls },
                                            label = { Text(cls, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BilindiBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                MediaAttachmentControl(
                                    sectionLabel = "Judul Modul",
                                    data = headerSection,
                                    onDataChange = { headerSection = it }
                                )
                            }
                        }

                        // 1. Pertanyaan Pemantik with Media Insertion
                        SectionInputBoxWithMedia(
                            title = "💡 1. Pertanyaan Pemantik",
                            badgeColor = LearningYellow,
                            placeholder = "Tuliskan pertanyaan pemantik untuk membangkitkan rasa ingin tahu siswa...",
                            sectionData = pemantikSection,
                            onDataChange = { pemantikSection = it }
                        )

                        // 2. Tujuan Pembelajaran with Media Insertion
                        SectionInputBoxWithMedia(
                            title = "🎯 2. Tujuan Pembelajaran",
                            badgeColor = LearningBlue,
                            placeholder = "Apa kompetensi dan pemahaman yang diharapkan dikuasai siswa?",
                            sectionData = tujuanSection,
                            onDataChange = { tujuanSection = it }
                        )

                        // 3. Materi Inti / Aktivitas with Media Insertion
                        SectionInputBoxWithMedia(
                            title = "📚 3. Materi Inti & Eksplorasi",
                            badgeColor = LearningGreen,
                            placeholder = "Jelaskan konsep utama, ringkasan materi, atau petunjuk eksperimen...",
                            sectionData = intiSection,
                            onDataChange = { intiSection = it }
                        )

                        // 4. Asesmen / Penguatan Konsep with Media Insertion
                        SectionInputBoxWithMedia(
                            title = "📝 4. Asesmen / LKPD / Refleksi",
                            badgeColor = LearningRed,
                            placeholder = "Tuliskan pertanyaan evaluasi, instruksi LKPD, atau refleksi belajar...",
                            sectionData = asesmenSection,
                            onDataChange = { asesmenSection = it }
                        )

                    } else {
                        // Tugas Mode
                        OutlinedTextField(
                            value = tugasJudul,
                            onValueChange = { tugasJudul = it },
                            label = { Text("Judul Tugas") },
                            placeholder = { Text("Contoh: Praktik Gerhana Bulan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = tugasDeskripsi,
                            onValueChange = { tugasDeskripsi = it },
                            label = { Text("Deskripsi & Instruksi Pengerjaan") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        // Quick select target class chips
                        Text(
                            text = "Pilih Kelas Target Tugas:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("VII-A", "VII-B", "VIII-B", "Semua Kelas").forEach { cls ->
                                FilterChip(
                                    selected = tugasKelas == cls,
                                    onClick = { tugasKelas = cls },
                                    label = { Text(cls, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LearningRed,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = tugasKelas,
                                onValueChange = { tugasKelas = it },
                                label = { Text("Kelas Target") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tugasDeadline,
                                onValueChange = { tugasDeadline = it },
                                label = { Text("Batas Waktu") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Attachment preview with PDF Preview Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LearningRed,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Lampiran Panduan Tugas:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(tugasAttachmentName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = { showTugasPdfPreview = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = LearningRed),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Generated Hashtag Preview Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BilindiBlueLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = BilindiBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hashtag Pengumpulan Otomatis:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BilindiBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = generatedHashtag,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BilindiBlueDark
                                )
                                Text(
                                    text = "Siswa yang memposting tugas dengan tagar ini akan otomatis masuk ke tabel penilaian Anda.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Footer Actions
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Batal")
                        }

                        Button(
                            onClick = {
                                if (selectedTab == 0) {
                                    val structured = StructuredLearningContent(
                                        headerSection = headerSection,
                                        pemantik = pemantikSection,
                                        tujuan = tujuanSection,
                                        inti = intiSection,
                                        asesmen = asesmenSection
                                    )
                                    onSubmitMateri(materiJudul, materiKelas, structured)
                                } else {
                                    onSubmitTugas(tugasJudul, tugasDeskripsi, tugasKelas, tugasDeadline, generatedHashtag, tugasAttachmentName)
                                }
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                            modifier = Modifier.testTag("submit_teacher_post_button")
                        ) {
                            Icon(if (initialPost != null) Icons.Default.Save else Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (initialPost != null) "Simpan Perubahan" else "Posting ke Dinding Kelas", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showTugasPdfPreview) {
        PdfPreviewDialog(
            documentTitle = tugasJudul,
            fileName = tugasAttachmentName,
            fileSize = "1.2 MB",
            pageCount = 3,
            onDismiss = { showTugasPdfPreview = false }
        )
    }
}

@Composable
private fun SectionInputBoxWithMedia(
    title: String,
    badgeColor: Color,
    placeholder: String,
    sectionData: SectionData,
    onDataChange: (SectionData) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(badgeColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = sectionData.text,
                onValueChange = { onDataChange(sectionData.copy(text = it)) },
                placeholder = { Text(placeholder, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            MediaAttachmentControl(
                sectionLabel = title,
                data = sectionData,
                onDataChange = onDataChange
            )
        }
    }
}
