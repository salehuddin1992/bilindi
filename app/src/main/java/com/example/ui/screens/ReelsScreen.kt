package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.ReelItem
import com.example.model.UserRole
import com.example.ui.components.CommentSheet
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@Composable
fun ReelsScreen(
    repository: BilindiWallRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState()
    val reels by repository.reels.collectAsState()
    val users by repository.users.collectAsState()
    val comments by repository.comments.collectAsState()

    var showCreateOrEditDialog by remember { mutableStateOf(false) }
    var reelToEdit by remember { mutableStateOf<ReelItem?>(null) }
    var activeCommentReel by remember { mutableStateOf<ReelItem?>(null) }

    val isTeacherOrAdmin = currentUser?.let { it.role == UserRole.TEACHER || it.role == UserRole.PRINCIPAL } ?: false

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("reels_screen"),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reels Header
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reels Edukasi & Simulasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Video pembelajaran ringkas & eksplorasi interaktif",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Guru Action Button: Buat Reel Edukasi Baru
                        if (isTeacherOrAdmin) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    reelToEdit = null
                                    showCreateOrEditDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("create_reel_button")
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buat Reel Edukasi Baru (Upload dari Galeri)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // List of Reels Cards
            items(reels) { reel ->
                val author = users[reel.authorId]
                var isPlaying by remember { mutableStateOf(true) }
                var showMenu by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                        .testTag("reel_card_${reel.id}")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Video Thumbnail Background
                        AsyncImage(
                            model = reel.thumbnailUrl,
                            contentDescription = reel.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Cinematic Gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.5f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.88f)
                                        )
                                    )
                                )
                        )

                        // Play / Pause center toggle
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Jeda" else "Putar",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Top Subject & Duration Pill
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = BilindiBlue.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = reel.subject,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = reel.duration,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Top Right: Edit/Delete Menu for Teacher/Admin
                        if (isTeacherOrAdmin) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        .testTag("reel_menu_${reel.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Opsi Reel",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit Reel") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            reelToEdit = reel
                                            showCreateOrEditDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Hapus Reel", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMenu = false
                                            repository.deleteReel(reel.id)
                                            Toast.makeText(context, "Reel '${reel.title}' dihapus", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }

                        // Bottom Details (Title, Description, Author)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth(0.80f)
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                ) {
                                    AsyncImage(
                                        model = author?.avatarUrl ?: "",
                                        contentDescription = author?.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = author?.name ?: "Pendidik",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = reel.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 20.sp
                            )

                            Text(
                                text = reel.description,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Vertical Interaction Column on Right (Like, Comments, Share)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { repository.toggleLikeReel(reel.id) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.45f))
                                ) {
                                    Icon(
                                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Suka",
                                        tint = if (reel.isLiked) Color(0xFFEF4444) else Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "${reel.likes}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Active Comment Button
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { activeCommentReel = reel },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .testTag("reel_comment_button_${reel.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ModeComment,
                                        contentDescription = "Komentar Reel",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "${reel.comments}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Tautan Reel Edukasi dibagikan!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Bagikan",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create or Edit Reel Dialog
    if (showCreateOrEditDialog) {
        CreateOrEditReelDialog(
            reel = reelToEdit,
            onDismiss = {
                showCreateOrEditDialog = false
                reelToEdit = null
            },
            onSave = { title, subject, duration, description, thumbnailUrl ->
                if (reelToEdit != null) {
                    val updated = reelToEdit!!.copy(
                        title = title,
                        subject = subject,
                        duration = duration,
                        description = description,
                        thumbnailUrl = thumbnailUrl
                    )
                    repository.updateReel(updated)
                    Toast.makeText(context, "Reel berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                } else {
                    val newReel = ReelItem(
                        id = "reel_" + System.currentTimeMillis(),
                        authorId = currentUser?.id ?: "u1",
                        title = title,
                        subject = subject,
                        duration = duration,
                        description = description,
                        thumbnailUrl = thumbnailUrl,
                        likes = 0,
                        comments = 0,
                        isLiked = false
                    )
                    repository.addReel(newReel)
                    Toast.makeText(context, "Reel edukasi baru berhasil diterbitkan!", Toast.LENGTH_SHORT).show()
                }
                showCreateOrEditDialog = false
                reelToEdit = null
            }
        )
    }

    // Active Comments Sheet for Reels
    activeCommentReel?.let { reel ->
        val user = currentUser ?: users.values.firstOrNull()
        if (user != null) {
            val reelComments = comments.filter { it.postId == reel.id }
            CommentSheet(
                title = "Diskusi: ${reel.title}",
                comments = reelComments,
                users = users,
                currentUser = user,
                onDismiss = { activeCommentReel = null },
                onSendComment = { text ->
                    repository.addComment(reel.id, text)
                }
            )
        }
    }
}

@Composable
private fun CreateOrEditReelDialog(
    reel: ReelItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, subject: String, duration: String, description: String, thumbnailUrl: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(reel?.title ?: "") }
    var subject by remember { mutableStateOf(reel?.subject ?: "IPA") }
    var duration by remember { mutableStateOf(reel?.duration ?: "0:45") }
    var description by remember { mutableStateOf(reel?.description ?: "") }
    var thumbnailUrl by remember {
        mutableStateOf(reel?.thumbnailUrl ?: "https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&q=80&w=800")
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            thumbnailUrl = uri.toString()
        }
    }

    val presetThumbnails = listOf(
        "https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&q=80&w=800" to "Kimia/Lab",
        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&q=80&w=800" to "Geometri",
        "https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&q=80&w=800" to "Biologi",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=800" to "Tata Surya",
        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=800" to "Literasi"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .testTag("create_or_edit_reel_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (reel != null) "Edit Reel Edukasi" else "Buat Reel Edukasi Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Video / Topik Reel") },
                    placeholder = { Text("cth: Eksperimen Reaksi Fotosintesis") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Subject & Duration row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Mata Pelajaran") },
                        placeholder = { Text("IPA / Fisika") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Durasi") },
                        placeholder = { Text("0:45") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Ringkas Materi") },
                    placeholder = { Text("Jelaskan poin utama konsep yang disampaikan pada video...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

                // Media / Thumbnail Upload Section
                Text(
                    text = "Video & Gambar Sampul:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp, 90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, BilindiBlue, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = "Thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                galleryPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_reel_gallery_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = LearningGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload dari Galeri HP", fontSize = 12.sp)
                        }
                        Text(
                            text = "Pilih video / foto konsep langsung dari memori perangkat.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Atau pilih aset simulasi edukasi:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetThumbnails) { (url, label) ->
                        val isSelected = thumbnailUrl == url
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BilindiBlue) else null,
                            modifier = Modifier
                                .width(65.dp)
                                .height(50.dp)
                                .clickable { thumbnailUrl = url }
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

                Spacer(modifier = Modifier.height(6.dp))

                // Save button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Silakan masukkan judul video!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onSave(title.trim(), subject.trim(), duration.trim(), description.trim(), thumbnailUrl)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_reel_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text(
                        text = if (reel != null) "Simpan Perubahan Reel" else "Terbitkan Reel Edukasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
