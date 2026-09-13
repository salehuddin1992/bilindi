package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.LearningGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    currentUser: User,
    initialHashtag: String = "",
    onDismiss: () -> Unit,
    onSubmitPost: (content: String, hashtag: String?, imageUrl: String?, targetClass: String?) -> Unit
) {
    val isStudent = currentUser.role == UserRole.STUDENT
    var contentText by remember { mutableStateOf("") }
    var hashtagText by remember { mutableStateOf(initialHashtag) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var targetClass by remember { mutableStateOf(if (isStudent) (currentUser.className ?: "VII-A") else "Semua Kelas") }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUrl = uri.toString()
        }
    }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1532692415740-42f0a149be54?auto=format&fit=crop&q=80&w=800" to "Gerhana",
        "https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&q=80&w=800" to "Eksperimen",
        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=800" to "Buku Catatan",
        "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&q=80&w=800" to "Laboratorium"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .testTag("create_post_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buat Postingan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // User Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (isStudent) {
                            Surface(
                                color = BilindiBlue.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = BilindiBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Terkunci: Kelas ${currentUser.className ?: "VII-A"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BilindiBlue
                                    )
                                }
                            }
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${currentUser.role.label} • Target: $targetClass",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // If teacher / principal, show quick target class selector
                if (!isStudent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Target Penerima Kiriman:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Semua Kelas", "VII-A", "VII-B", "VIII-B").forEach { cls ->
                            FilterChip(
                                selected = targetClass == cls,
                                onClick = { targetClass = cls },
                                label = { Text(cls, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BilindiBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Post Text Field
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    placeholder = { Text("Tulis refleksi belajar, hasil eksperimen, atau tanggapan tugas...", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("post_content_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Optional Hashtag
                OutlinedTextField(
                    value = hashtagText,
                    onValueChange = { hashtagText = it },
                    placeholder = { Text("Hashtag tugas (misal: #TugasIPA7Gerhana)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = BilindiBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Photo Picker Header with Gallery Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lampirkan Foto / Bukti:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("upload_status_gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = LearningGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dari Galeri", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Selected Image Preview (if uploaded from gallery or selected)
                if (selectedImageUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = selectedImageUrl,
                            contentDescription = "Preview Foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { selectedImageUrl = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus foto",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Atau pilih gambar preset sekolah:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetImages) { (url, label) ->
                        val isSelected = selectedImageUrl == url
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BilindiBlue) else null,
                            modifier = Modifier
                                .width(80.dp)
                                .height(60.dp)
                                .clickable {
                                    selectedImageUrl = if (isSelected) null else url
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = label,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(BilindiBlue.copy(alpha = 0.35f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action button
                Button(
                    onClick = {
                        if (contentText.isNotBlank() || selectedImageUrl != null) {
                            val tag = if (hashtagText.isNotBlank()) {
                                if (hashtagText.startsWith("#")) hashtagText else "#$hashtagText"
                            } else null
                            onSubmitPost(contentText, tag, selectedImageUrl, targetClass)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("publish_post_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text("Posting", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
