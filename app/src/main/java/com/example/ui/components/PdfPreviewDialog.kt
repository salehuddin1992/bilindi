package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueDark
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen
import com.example.ui.theme.LearningRed

@Composable
fun PdfPreviewDialog(
    documentTitle: String,
    fileName: String,
    fileSize: String = "1.8 MB",
    pageCount: Int = 4,
    subject: String = "IPA / Modul Ajar",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(1) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("pdf_preview_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = LearningRed,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = fileName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = "$fileSize • $subject • Hal $currentPage dari $pageCount",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Zoom toggle
                            IconButton(
                                onClick = {
                                    zoomScale = when (zoomScale) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        else -> 1.0f
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (zoomScale > 1.0f) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                    contentDescription = "Zoom",
                                    tint = BilindiBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Share / Open External Intent
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse("https://bilindiwall.sch.id/docs/$fileName"), "application/pdf")
                                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Membuka dokumen '$fileName'", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Buka File",
                                    tint = BilindiBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Page navigation quick switcher chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val pageTitles = listOf(
                        "Hal 1: Identitas & CP",
                        "Hal 2: Materi & Pemantik",
                        "Hal 3: LKPD Siswa",
                        "Hal 4: Rubrik Asesmen"
                    )
                    pageTitles.forEachIndexed { index, title ->
                        val pageNum = index + 1
                        val isSelected = currentPage == pageNum
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) BilindiBlue else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BilindiBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .height(30.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            TextButton(
                                onClick = { currentPage = pageNum },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Document Paper Canvas Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0)) // Neutral document viewer background
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Realistic Document Page
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(if (zoomScale > 1.0f) 1.0f else 0.98f)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp)),
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            when (currentPage) {
                                1 -> PageOneIdentitas(documentTitle, fileName)
                                2 -> PageTwoMateriInti(documentTitle)
                                3 -> PageThreeLKPD(documentTitle)
                                4 -> PageFourAsesmen(documentTitle)
                                else -> PageOneIdentitas(documentTitle, fileName)
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Dokumen Terverifikasi • Bilindi Digital Wall SMPN 1",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                                Text(
                                    text = "- Halaman $currentPage dari $pageCount -",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }

                // Bottom Pagination Controller
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sebelumnya", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hal Sebelumnya", fontSize = 12.sp)
                        }

                        Text(
                            text = "Halaman $currentPage / $pageCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = { if (currentPage < pageCount) currentPage++ },
                            enabled = currentPage < pageCount,
                            colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Hal Selanjutnya", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Selanjutnya", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageOneIdentitas(documentTitle: String, fileName: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Kop Surat Resmi
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BilindiBlue,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "KEMENTERIAN PENDIDIKAN, KEBUDAYAAN, RISET DAN TEKNOLOGI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "SMP NEGERI 1 DIGITAL INDONESIA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = BilindiBlueDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Jalan Pendidikan Digital No. 45, Jakarta • Surel: kurikulum@smpn1digital.sch.id",
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color.Black))
        Spacer(modifier = Modifier.height(2.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
        Spacer(modifier = Modifier.height(14.dp))

        // Title of Module
        Text(
            text = "MODUL AJAR KURIKULUM MERDEKA",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = documentTitle.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = BilindiBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metadata Table
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            color = Color(0xFFF8FAFC),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DocMetaRow("Mata Pelajaran", "Ilmu Pengetahuan Alam (IPA)")
                DocMetaRow("Fase / Kelas", "Fase D / Kelas VII (Tujuh)")
                DocMetaRow("Semester / TP", "Semester Ganjil / 2026-2027")
                DocMetaRow("Alokasi Waktu", "2 x 40 Menit (1 Pertemuan)")
                DocMetaRow("Penyusun", "Dra. Sri Wahyuni, M.Pd. (Guru Penggerak)")
                DocMetaRow("Nama Berkas", fileName)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "A. CAPAIAN PEMBELAJARAN & TUJUAN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "Peserta didik memahami sistem tata surya, karakteristik anggota tata surya, orbit planet mengelilingi matahari, serta keterkaitannya dengan gravitasi semesta dan fenomena alam di bumi.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "B. PROFIL PELAJAR PANCASILA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "• Bernalar Kritis: Menganalisis alasan planet tidak bertabrakan saat mengorbit.\n• Mandiri: Menyelesaikan lembar pengamatan laboratorium mandiri.\n• Gotong Royong: Berdiskusi dalam kelompok merumuskan hipotesis fenomena antariksa.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PageTwoMateriInti(documentTitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "KEGIATAN PEMBELAJARAN & MATERI ESENSIAL",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = BilindiBlueDark
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFCBD5E1)))
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "1. PERTANYAAN PEMANTIK (APERSEPSI)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFFEF9C3),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFACC15)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text(
                text = "\"Pernahkah kalian melihat bintang jatuh di langit malam? Sebenarnya apa itu bintang jatuh? Dan mengapa planet-planet mengitari matahari secara teratur tanpa pernah bertabrakan?\"",
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF713F12),
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "2. EKSPLORASI KONSEP UTAMA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "Sistem Tata Surya kita terdiri atas Matahari sebagai pusat gravitasi massa, 8 planet utama, komet, meteoroid, dan sabuk asteroid. Gravitasi matahari menjaga keseimbangan orbit setiap planet berdasarkan hukum Kepler dan gravitasi universal Newton.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ringkasan Tabel Karakteristik Planet
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF94A3B8)),
            color = Color(0xFFF1F5F9),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Tabel Pengelompokan Planet:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                DocTableRow("Planet Terestrial (Kebumian)", "Merkurius, Venus, Bumi, Mars (Padat berbatu)")
                DocTableRow("Planet Jovian (Raksasa Gas)", "Jupiter, Saturnus, Uranus, Neptunus")
                DocTableRow("Pembatas Orbit", "Sabuk Asteroid (antara Mars & Jupiter)")
            }
        }
    }
}

@Composable
private fun PageThreeLKPD(documentTitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LEMBAR KERJA PESERTA DIDIK (LKPD)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = LearningGreen
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFCBD5E1)))
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Petunjuk Kerja Siswa:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "1. Buka simulasi PhET atau video pembelajaran yang disisipkan oleh guru di BilindiWall.\n2. Catat dan amati hubungan jarak orbit dengan periode revolusi planet.\n3. Lengkapi tabel pengamatan di bawah ini dengan tepat.",
            fontSize = 11.sp,
            color = Color(0xFF334155),
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LKPD Table
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(6.dp)
                ) {
                    Text("No", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.width(24.dp))
                    Text("Objek Planet", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1.2f))
                    Text("Jarak (AU)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    Text("Karakteristik Kunci", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1.8f))
                }
                DocLkpdRow("1", "Merkurius", "0.39 AU", "Suhu ekstrim, tanpa atmosfer")
                DocLkpdRow("2", "Venus", "0.72 AU", "Efek rumah kaca terkuat, terpanas")
                DocLkpdRow("3", "Bumi", "1.00 AU", "Ada air cair & atmosfer nitrogen-oksigen")
                DocLkpdRow("4", "Mars", "1.52 AU", "Planet merah, kaya oksida besi")
                DocLkpdRow("5", "Jupiter", "5.20 AU", "Planet terbesar, bintik merah raksasa")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Pertanyaan Analisis Mandiri:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "\"Mengapa semakin jauh posisi sebuah planet dari matahari, waktu yang dibutuhkan untuk mengelilingi matahari menjadi semakin lama? Hubungkan jawabanmu dengan konsep gravitasi!\"",
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PageFourAsesmen(documentTitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "RUBRIK ASESMEN & REFLEKSI PEMBELAJARAN",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = LearningRed
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFCBD5E1)))
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "1. KRITERIA KETERCAPAIAN TUJUAN PEMBELAJARAN (KKTP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        DocTableRow("Sangat Mahir (90-100)", "Mampu memodelkan gaya gravitasi dan orbit 8 planet secara matematis dan konseptual.")
        DocTableRow("Mahir (75-89)", "Mampu mengelompokkan planet dan menjelaskan alasan stabilitas orbit tata surya.")
        DocTableRow("Perlu Bimbingan (<75)", "Belum dapat menghafal urutan planet dan konsep dasar gravitasi matahari.")

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "2. LEMBAR REFLEKSI SISWA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(
            text = "Bagikan refleksi pembelajaran di kolom komentar postingan materi ini dengan format: \n[Nama Lengkap] - [Satu hal paling menakjubkan yang dipelajari hari ini].",
            fontSize = 10.sp,
            color = Color(0xFF475569),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Digital Signature & Approval
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Mengetahui,", fontSize = 10.sp, color = Color.DarkGray)
                Text("Kepala SMP Negeri sinombayuga,", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Dr. Hendra Gunawan, M.Pd.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("NIP. 19750812 199903 1 004", fontSize = 9.sp, color = Color.DarkGray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Jakarta, September 2026", fontSize = 10.sp, color = Color.DarkGray)
                Text("Guru Mata Pelajaran IPA,", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Dra. Sri Wahyuni, M.Pd.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("NIP. 19820415 200801 2 011", fontSize = 9.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun DocMetaRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.width(110.dp))
        Text(text = ": ", fontSize = 10.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DocTableRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(text = "• $title", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.width(150.dp))
        Text(text = ": $desc", fontSize = 10.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DocLkpdRow(no: String, objectName: String, dist: String, char: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color(0xFFCBD5E1))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(no, fontSize = 10.sp, color = Color.Black, modifier = Modifier.width(24.dp))
        Text(objectName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BilindiBlueDark, modifier = Modifier.weight(1.2f))
        Text(dist, fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
        Text(char, fontSize = 10.sp, color = Color(0xFF334155), modifier = Modifier.weight(1.8f))
    }
}
