package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.Post
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.components.ChangeAvatarDialog
import com.example.ui.components.PostCardItem
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen
import com.example.ui.theme.LearningYellow

@Composable
fun ProfileScreen(
    repository: BilindiWallRepository,
    currentUser: User,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by repository.posts.collectAsState()
    val users by repository.users.collectAsState()

    var showEditBioDialog by remember { mutableStateOf(false) }
    var showChangeAvatarDialog by remember { mutableStateOf(false) }
    var showBadgeDialog by remember { mutableStateOf(false) }
    var currentBio by remember { mutableStateOf(currentUser.bio) }

    val userPosts = posts.filter { it.authorId == currentUser.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Facebook Style Cover & Avatar Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                // Cover Image
                AsyncImage(
                    model = currentUser.coverUrl,
                    contentDescription = "Foto Sampul",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )

                // Overlapping Avatar with Camera / Upload Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 16.dp, y = 0.dp)
                        .size(108.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showChangeAvatarDialog = true }
                            .testTag("profile_avatar")
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Camera Badge to change photo
                    IconButton(
                        onClick = { showChangeAvatarDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BilindiBlue)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .testTag("profile_avatar_upload_badge")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Upload Foto Profil",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Edit Profile / Settings Icon Top Right
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Profile Identity Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = currentUser.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${currentUser.role.label} • ${
                        if (currentUser.role == UserRole.TEACHER) currentUser.subject ?: "Guru"
                        else if (currentUser.role == UserRole.PRINCIPAL) "Kepala Sekolah"
                        else "Kelas ${currentUser.className ?: ""}"
                    }",
                    fontSize = 13.sp,
                    color = BilindiBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons: Ganti Foto Profil & Edit Bio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showChangeAvatarDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("button_change_avatar"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upload Foto",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { showEditBioDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Bio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Perkenalan / Bio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${currentUser.bio}\"",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Details info: School, Location
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = BilindiBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Belajar di ${currentUser.school}", fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tinggal di ${currentUser.location}", fontSize = 13.sp)
                    }
                }
            }
        }

        // Achievement Badges (Lencana Prestasi)
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = LearningYellow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lencana Prestasi & Karakter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        TextButton(
                            onClick = { showBadgeDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Kelola",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BilindiBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentUser.badges.isEmpty()) {
                        Text(
                            text = "Belum ada lencana yang disematkan. Klik 'Kelola' untuk memilih lencana prestasi.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            currentUser.badges.forEach { badge ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (badge) {
                                        "Rajin", "Bintang Kelas" -> LearningYellow.copy(alpha = 0.15f)
                                        "Kreatif", "Teladan Literasi" -> LearningGreen.copy(alpha = 0.15f)
                                        else -> BilindiBlueLight
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = when (badge) {
                                                "Rajin", "Bintang Kelas" -> LearningYellow
                                                "Kreatif", "Teladan Literasi" -> LearningGreen
                                                else -> BilindiBlue
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = badge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Postingan Saya
        item {
            Text(
                text = "Postingan Saya (${userPosts.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (userPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada postingan yang Anda bagikan.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(userPosts) { post ->
                PostCardItem(
                    post = post,
                    author = currentUser,
                    onLikeClick = { repository.toggleLikePost(post.id) },
                    onCommentClick = {},
                    onShareClick = {
                        Toast.makeText(context, "Tautan disalin!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    // Edit Bio Dialog
    if (showEditBioDialog) {
        var newBioText by remember { mutableStateOf(currentUser.bio) }
        Dialog(onDismissRequest = { showEditBioDialog = false }) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Edit Bio Profil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newBioText,
                        onValueChange = { newBioText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showEditBioDialog = false }) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                repository.updateUserProfile(
                                    name = currentUser.name,
                                    avatarUrl = currentUser.avatarUrl,
                                    coverUrl = currentUser.coverUrl,
                                    bio = newBioText
                                )
                                showEditBioDialog = false
                                Toast.makeText(context, "Bio profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }

    // Change Avatar / Upload Profile Photo Dialog
    if (showChangeAvatarDialog) {
        ChangeAvatarDialog(
            user = currentUser,
            onAvatarUpdated = { newUrl ->
                repository.updateCurrentUserAvatar(newUrl)
            },
            onDismiss = { showChangeAvatarDialog = false }
        )
    }

    // Badge Selection / Management Dialog
    if (showBadgeDialog) {
        BadgeSelectionDialog(
            currentUser = currentUser,
            onDismiss = { showBadgeDialog = false },
            onSaveBadges = { newBadges ->
                repository.updateUserBadges(currentUser.id, newBadges)
                showBadgeDialog = false
                Toast.makeText(context, "Lencana berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun BadgeSelectionDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSaveBadges: (List<String>) -> Unit
) {
    val availableBadges = if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
        listOf("Guru Berprestasi", "Inovator Media", "Pendidik Dedikatif", "Pembina Utama", "Inspiratif", "Penggerak Literasi")
    } else {
        listOf("Rajin", "Kreatif", "Sportif", "Bintang Kelas", "Teladan Literasi", "Juara Sains", "Aktif Berdiskusi")
    }

    var selectedBadges by remember { mutableStateOf(currentUser.badges.toSet()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kelola Lencana Prestasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Pilih tanda apresiasi untuk profil Anda",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableBadges.forEach { badge ->
                        val isSelected = selectedBadges.contains(badge)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BilindiBlueLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BilindiBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedBadges = if (isSelected) {
                                        selectedBadges - badge
                                    } else {
                                        selectedBadges + badge
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = if (isSelected) BilindiBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = badge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) BilindiBlue else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSaveBadges(selectedBadges.toList()) },
                        colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                    ) {
                        Text("Terapkan")
                    }
                }
            }
        }
    }
}
