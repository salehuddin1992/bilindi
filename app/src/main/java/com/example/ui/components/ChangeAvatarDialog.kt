package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@Composable
fun ChangeAvatarDialog(
    user: User,
    onAvatarUpdated: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var previewUrl by remember { mutableStateOf(user.avatarUrl) }
    var customUrlInput by remember { mutableStateOf("") }
    var showUrlInput by remember { mutableStateOf(false) }

    // Native Android Photo Picker launcher (zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            previewUrl = uri.toString()
            Toast.makeText(context, "Foto berhasil dipilih dari galeri!", Toast.LENGTH_SHORT).show()
        }
    }

    val recommendedAvatars = if (user.role == UserRole.TEACHER || user.role == UserRole.PRINCIPAL) {
        listOf(
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300", // Teacher 1
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300", // Principal
            "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=300", // Teacher 2
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&q=80&w=300", // Teacher 3
            "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?auto=format&fit=crop&q=80&w=300"  // Teacher 4
        )
    } else {
        listOf(
            "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=300", // Student 1
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300", // Student 2
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=300", // Student 3
            "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300", // Student 4
            "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&q=80&w=300"  // Student 5
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("change_avatar_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ganti Foto Profil",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${user.name} (${user.role.label})",
                            fontSize = 12.sp,
                            color = BilindiBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Preview with camera overlay
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(4.dp, BilindiBlue, CircleShape)
                        .background(BilindiBlueLight)
                ) {
                    AsyncImage(
                        model = previewUrl,
                        contentDescription = "Preview Foto Profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    text = "Pratinjau Foto Baru",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // Option 1: Primary Upload Button (Photo Picker)
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("upload_from_gallery_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unggah dari Galeri / HP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: Choose from curated presets for Teacher or Student
                Text(
                    text = if (user.role == UserRole.TEACHER || user.role == UserRole.PRINCIPAL)
                        "Atau Pilih Foto Profil Guru/Tenaga Pendidik:"
                    else
                        "Atau Pilih Avatar Siswa Siap Pakai:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recommendedAvatars) { url ->
                        val isSelected = previewUrl == url
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) LearningGreen else Color.LightGray,
                                    CircleShape
                                )
                                .clickable { previewUrl = url }
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Terpilih",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option 3: URL input toggle
                TextButton(
                    onClick = { showUrlInput = !showUrlInput },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (showUrlInput) Icons.Default.ExpandLess else Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = BilindiBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showUrlInput) "Sembunyikan Input URL" else "Masukkan URL Foto Manual",
                        fontSize = 12.sp,
                        color = BilindiBlue
                    )
                }

                if (showUrlInput) {
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = {
                            customUrlInput = it
                            if (it.startsWith("http://") || it.startsWith("https://")) {
                                previewUrl = it
                            }
                        },
                        label = { Text("https://... (URL Foto)") },
                        placeholder = { Text("Tempel link foto online") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (customUrlInput.isNotBlank()) {
                                IconButton(onClick = {
                                    previewUrl = customUrlInput
                                    Toast.makeText(context, "URL diterapkan ke pratinjau", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Check, contentDescription = "Terapkan")
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            onAvatarUpdated(previewUrl)
                            Toast.makeText(
                                context,
                                "Foto profil ${user.name} berhasil diperbarui!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .testTag("save_avatar_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LearningGreen)
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Foto", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
