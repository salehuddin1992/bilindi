package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.SectionData
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen
import com.example.ui.theme.LearningRed
import com.example.ui.theme.LearningYellow

@Composable
fun MediaAttachmentControl(
    sectionLabel: String,
    data: SectionData,
    onDataChange: (SectionData) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImageDialog by remember { mutableStateOf(false) }
    var showVideoDialog by remember { mutableStateOf(false) }
    var showEmbedDialog by remember { mutableStateOf(false) }
    var showPdfDialog by remember { mutableStateOf(false) }
    var showPdfPreview by remember { mutableStateOf(false) }

    // Native Photo Picker for gallery images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onDataChange(data.copy(imageUrl = uri.toString()))
            Toast.makeText(context, "Foto berhasil disisipkan dari galeri!", Toast.LENGTH_SHORT).show()
        }
    }

    // Native Document Picker for PDF files
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val displayName = uri.lastPathSegment?.substringAfterLast("/") ?: "Dokumen_Materi_Guru.pdf"
            onDataChange(data.copy(pdfUrl = uri.toString(), pdfName = if (displayName.endsWith(".pdf")) displayName else "$displayName.pdf"))
            Toast.makeText(context, "Berkas PDF berhasil diunggah!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Media Toolbar Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sisipkan:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Button 1: Image
            FilterChip(
                selected = !data.imageUrl.isNullOrEmpty(),
                onClick = { showImageDialog = true },
                label = { Text("Gambar", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gambar",
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BilindiBlueLight,
                    selectedLabelColor = BilindiBlue
                ),
                modifier = Modifier.height(28.dp)
            )

            // Button 2: Video
            FilterChip(
                selected = !data.videoUrl.isNullOrEmpty(),
                onClick = { showVideoDialog = true },
                label = { Text("Video", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video",
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFEE2E2),
                    selectedLabelColor = LearningRed
                ),
                modifier = Modifier.height(28.dp)
            )

            // Button 3: Embed
            FilterChip(
                selected = !data.embedUrl.isNullOrEmpty(),
                onClick = { showEmbedDialog = true },
                label = { Text("Embed", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Embed",
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFDCFCE7),
                    selectedLabelColor = LearningGreen
                ),
                modifier = Modifier.height(28.dp)
            )

            // Button 4: Upload PDF
            FilterChip(
                selected = !data.pdfName.isNullOrEmpty(),
                onClick = { showPdfDialog = true },
                label = { Text("Upload PDF", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFEF3C7),
                    selectedLabelColor = Color(0xFFD97706)
                ),
                modifier = Modifier.height(28.dp)
            )
        }

        // Active Attachments Display (Cards for attached Image, Video, Embed, PDF)
        if (!data.imageUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BilindiBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(BilindiBlueLight)
                    ) {
                        AsyncImage(
                            model = data.imageUrl,
                            contentDescription = "Pratinjau Gambar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🖼️ Gambar Disisipkan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BilindiBlue)
                        Text(data.imageUrl ?: "", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                    IconButton(
                        onClick = { onDataChange(data.copy(imageUrl = null)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus Gambar", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (!data.videoUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFF1F2),
                border = androidx.compose.foundation.BorderStroke(1.dp, LearningRed.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LearningRed,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🎥 Video Pembelajaran", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LearningRed)
                        Text(data.videoUrl ?: "", fontSize = 10.sp, color = Color.DarkGray, maxLines = 1)
                    }
                    IconButton(
                        onClick = { onDataChange(data.copy(videoUrl = null)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus Video", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (!data.embedUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, LearningGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LearningGreen,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🌐 Embed Interaktif (PhET/Web)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LearningGreen)
                        Text(data.embedUrl ?: "", fontSize = 10.sp, color = Color.DarkGray, maxLines = 1)
                    }
                    IconButton(
                        onClick = { onDataChange(data.copy(embedUrl = null)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus Embed", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (!data.pdfName.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFFBEB),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
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
                        Text("📄 ${data.pdfName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), maxLines = 1)
                        Text("Dokumen PDF • 4 Halaman Lengkap", fontSize = 10.sp, color = Color.Gray)
                    }

                    // Button to Preview PDF
                    Button(
                        onClick = { showPdfPreview = true },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearningRed),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { onDataChange(data.copy(pdfName = null, pdfUrl = null)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus PDF", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Modal: Sisipkan Gambar
    if (showImageDialog) {
        var inputUrl by remember { mutableStateOf(data.imageUrl ?: "") }
        val sampleImages = listOf(
            "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?auto=format&fit=crop&q=80&w=600" to "Tata Surya & Galaksi",
            "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&q=80&w=600" to "Bintang Jatuh & Langit",
            "https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&q=80&w=600" to "Laboratorium Sains",
            "https://images.unsplash.com/photo-1516339901601-2e1b62dc0c45?auto=format&fit=crop&q=80&w=600" to "Teleskop Antariksa"
        )

        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Sisipkan Gambar ke $sectionLabel", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            showImageDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih dari Galeri / HP", fontWeight = FontWeight.Bold)
                    }

                    Text("Atau Pilih Contoh Ilustrasi Sains:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sampleImages) { (url, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(90.dp)
                                    .clickable {
                                        onDataChange(data.copy(imageUrl = url))
                                        showImageDialog = false
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(model = url, contentDescription = label, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                }
                                Text(label, fontSize = 9.sp, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("Atau Tempel Link Gambar (URL)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputUrl.isNotBlank()) {
                            onDataChange(data.copy(imageUrl = inputUrl))
                        }
                        showImageDialog = false
                    }
                ) {
                    Text("Terapkan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImageDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal: Sisipkan Video
    if (showVideoDialog) {
        var videoUrlInput by remember { mutableStateOf(data.videoUrl ?: "https://www.youtube.com/watch?v=libKVRa01L8") }
        val sampleVideos = listOf(
            "https://www.youtube.com/watch?v=libKVRa01L8" to "Simulasi Tata Surya 3D",
            "https://www.youtube.com/watch?v=wWbLzPvdF_A" to "Gerak Gravitasi Planet",
            "https://www.youtube.com/watch?v=21X5lGlDOfg" to "Animasi Gerhana Bulan NASA"
        )

        AlertDialog(
            onDismissRequest = { showVideoDialog = false },
            title = { Text("Sisipkan Video ke $sectionLabel", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pilih Rekomendasi Video Pembelajaran:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    sampleVideos.forEach { (url, label) ->
                        Surface(
                            onClick = { videoUrlInput = url },
                            shape = RoundedCornerShape(6.dp),
                            color = if (videoUrlInput == url) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (videoUrlInput == url) LearningRed else Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = LearningRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = videoUrlInput,
                        onValueChange = { videoUrlInput = it },
                        label = { Text("Link Video (YouTube / MP4)") },
                        leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = LearningRed) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (videoUrlInput.isNotBlank()) {
                            onDataChange(data.copy(videoUrl = videoUrlInput))
                        }
                        showVideoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LearningRed)
                ) {
                    Text("Simpan Video")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showVideoDialog = false }) { Text("Batal") }
            }
        )
    }

    // Modal: Sisipkan Embed
    if (showEmbedDialog) {
        var embedUrlInput by remember { mutableStateOf(data.embedUrl ?: "https://phet.colorado.edu/sims/html/gravity-and-orbits/latest/gravity-and-orbits_en.html") }
        val sampleEmbeds = listOf(
            "https://phet.colorado.edu/sims/html/gravity-and-orbits/latest/gravity-and-orbits_en.html" to "PhET: Gravitasi & Orbit Interaktif",
            "https://phet.colorado.edu/sims/html/my-solar-system/latest/my-solar-system_en.html" to "PhET: Buat Tata Surya Sendiri",
            "https://quizizz.com/join/quiz/sains-tata-surya" to "Quizizz: Kuis Refleksi Bab Tata Surya"
        )

        AlertDialog(
            onDismissRequest = { showEmbedDialog = false },
            title = { Text("Sisipkan Embed / Simulasi ke $sectionLabel", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pilihan Laboratorium Maya & Kuis Interaktif:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    sampleEmbeds.forEach { (url, label) ->
                        Surface(
                            onClick = { embedUrlInput = url },
                            shape = RoundedCornerShape(6.dp),
                            color = if (embedUrlInput == url) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (embedUrlInput == url) LearningGreen else Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = LearningGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = embedUrlInput,
                        onValueChange = { embedUrlInput = it },
                        label = { Text("Link Embed Web / Simulasi / Canva") },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = LearningGreen) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (embedUrlInput.isNotBlank()) {
                            onDataChange(data.copy(embedUrl = embedUrlInput))
                        }
                        showEmbedDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LearningGreen)
                ) {
                    Text("Simpan Embed")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEmbedDialog = false }) { Text("Batal") }
            }
        )
    }

    // Modal: Upload & Preview PDF
    if (showPdfDialog) {
        var customPdfName by remember { mutableStateOf(data.pdfName ?: "Modul_Ajar_IPA_Sistem_Tata_Surya.pdf") }
        val samplePdfs = listOf(
            "Modul_Ajar_IPA_Sistem_Tata_Surya.pdf" to "Modul Ajar Kurikulum Merdeka (4 Hal)",
            "LKPD_Pengamatan_Fenomena_Antariksa.pdf" to "LKPD Lembar Kerja Siswa Terstruktur",
            "Panduan_Praktikum_Simulasi_Gravitasi.pdf" to "Panduan Eksperimen Laboratorium Maya"
        )

        AlertDialog(
            onDismissRequest = { showPdfDialog = false },
            title = { Text("Upload Berkas PDF ke $sectionLabel", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Upload button from local phone storage
                    Button(
                        onClick = {
                            showPdfDialog = false
                            try {
                                docPickerLauncher.launch(arrayOf("application/pdf"))
                            } catch (_: Exception) {
                                Toast.makeText(context, "Penyimpanan perangkat dibuka", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Berkas PDF dari HP / File Manager", fontWeight = FontWeight.Bold)
                    }

                    Text("Atau Gunakan Template Modul Ajar Resmi:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    samplePdfs.forEach { (name, label) ->
                        Surface(
                            onClick = { customPdfName = name },
                            shape = RoundedCornerShape(6.dp),
                            color = if (customPdfName == name) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (customPdfName == name) Color(0xFFD97706) else Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = LearningRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = customPdfName,
                        onValueChange = { customPdfName = it },
                        label = { Text("Nama Berkas PDF") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = LearningRed) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview Button right inside upload dialog!
                    OutlinedButton(
                        onClick = {
                            onDataChange(data.copy(pdfName = customPdfName, pdfUrl = "https://bilindiwall.sch.id/docs/$customPdfName"))
                            showPdfPreview = true
                        }
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview PDF")
                    }

                    Button(
                        onClick = {
                            onDataChange(data.copy(pdfName = customPdfName, pdfUrl = "https://bilindiwall.sch.id/docs/$customPdfName"))
                            showPdfDialog = false
                            Toast.makeText(context, "Dokumen PDF '$customPdfName' berhasil dilampirkan!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                    ) {
                        Text("Lampirkan")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPdfDialog = false }) { Text("Batal") }
            }
        )
    }

    // PDF Preview Viewer Dialog
    if (showPdfPreview) {
        PdfPreviewDialog(
            documentTitle = "Modul: $sectionLabel",
            fileName = data.pdfName ?: "Dokumen_Materi_Guru.pdf",
            fileSize = "1.8 MB",
            pageCount = 4,
            onDismiss = { showPdfPreview = false }
        )
    }
}
