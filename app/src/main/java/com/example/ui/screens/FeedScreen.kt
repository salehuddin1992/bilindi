package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BilindiWallRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight

@Composable
fun FeedScreen(
    repository: BilindiWallRepository,
    currentUser: User,
    searchQuery: String,
    onHashtagFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by repository.posts.collectAsState()
    val users by repository.users.collectAsState()
    val stories by repository.stories.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val comments by repository.comments.collectAsState()
    val selectedClassFilter by repository.selectedClassFilter.collectAsState()

    var selectedFilter by remember { mutableStateOf("Semua") }
    var activeHashtagFilter by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var showTeacherModal by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var activeCommentPost by remember { mutableStateOf<Post?>(null) }
    var showModerationToast by remember { mutableStateOf(false) }
    var postToEdit by remember { mutableStateOf<Post?>(null) }

    // Filter stories based on class relevance
    val filteredStories = stories.filter { story ->
        val author = users[story.authorId]
        val storyClass = story.targetClass ?: author?.className ?: "Semua Kelas"
        if (currentUser.role == UserRole.STUDENT) {
            val studentClass = currentUser.className ?: "VII-A"
            storyClass.equals(studentClass, ignoreCase = true) ||
            (author?.role == UserRole.STUDENT && author.className.equals(studentClass, ignoreCase = true)) ||
            author?.role == UserRole.TEACHER ||
            author?.role == UserRole.PRINCIPAL ||
            storyClass == "Semua Kelas"
        } else {
            if (selectedClassFilter == "Semua Kelas" || selectedClassFilter.isBlank()) {
                true
            } else {
                storyClass.equals(selectedClassFilter, ignoreCase = true) ||
                (author?.role == UserRole.STUDENT && author.className.equals(selectedClassFilter, ignoreCase = true)) ||
                storyClass == "Semua Kelas"
            }
        }
    }

    // Filter posts based on class, search, category filter, and hashtag
    val availableClasses by repository.availableClasses.collectAsState()

    val filteredPosts = posts.filter { post ->
        val author = users[post.authorId]
        val postClass = post.targetClass 
            ?: post.assignmentDetail?.targetClass 
            ?: author?.className 
            ?: "Semua Kelas"

        val matchesClass = if (currentUser.role == UserRole.STUDENT) {
            val studentClass = currentUser.className ?: "VII-A"
            // Student is STRICTLY LOCKED to their class, classmates, and school-wide posts
            postClass.equals(studentClass, ignoreCase = true) ||
            (author?.role == UserRole.STUDENT && author.className.equals(studentClass, ignoreCase = true)) ||
            postClass == "Semua Kelas" ||
            author?.role == UserRole.PRINCIPAL
        } else {
            // Teacher / Principal chooses class filter
            if (selectedClassFilter == "Semua Kelas" || selectedClassFilter.isBlank()) {
                true
            } else {
                postClass.equals(selectedClassFilter, ignoreCase = true) ||
                (author?.role == UserRole.STUDENT && author.className.equals(selectedClassFilter, ignoreCase = true)) ||
                postClass == "Semua Kelas" ||
                author?.role == UserRole.PRINCIPAL
            }
        }

        val matchesSearch = if (searchQuery.isBlank()) true else {
            (post.title?.contains(searchQuery, ignoreCase = true) == true) ||
            (post.content?.contains(searchQuery, ignoreCase = true) == true) ||
            (post.hashtag?.contains(searchQuery, ignoreCase = true) == true) ||
            (author?.name?.contains(searchQuery, ignoreCase = true) == true)
        }

        val matchesHashtag = if (activeHashtagFilter == null) true else {
            post.hashtag.equals(activeHashtagFilter, ignoreCase = true)
        }

        val matchesCategory = when (selectedFilter) {
            "Materi" -> post.type == PostType.LEARNING
            "Tugas" -> post.type == PostType.ASSIGNMENT
            "Siswa" -> post.type == PostType.STATUS && author?.role == UserRole.STUDENT
            else -> true
        }

        matchesClass && matchesSearch && matchesHashtag && matchesCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Stories Section
        item {
            StoryCarousel(
                currentUser = currentUser,
                stories = filteredStories,
                users = users,
                onAddStoryClick = { showAddStoryDialog = true }
            )
        }

        // 2. Class Filter Bar (Locked for student, interactive selector for teacher)
        item {
            ClassFilterBar(
                currentUser = currentUser,
                selectedClass = selectedClassFilter,
                availableClasses = availableClasses,
                onClassSelected = { repository.setSelectedClassFilter(it) },
                onAddClass = if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
                    { newClass -> repository.addClass(newClass) }
                } else null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 2. School Announcement Banner
        announcements.firstOrNull()?.let { ann ->
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pengumuman: ${ann.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ann.content,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Create Post Trigger Box
        item {
            CreatePostCard(
                currentUser = currentUser,
                onOpenCreatePost = { showCreatePostDialog = true },
                onOpenTeacherModal = { showTeacherModal = true },
                onOpenPhotoPost = { showCreatePostDialog = true },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // 4. Hashtag active filter indicator (if applied)
        activeHashtagFilter?.let { tag ->
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BilindiBlueLight,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menampilkan kiriman dengan tagar: $tag",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BilindiBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus filter",
                            tint = BilindiBlue,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { activeHashtagFilter = null }
                        )
                    }
                }
            }
        }

        // 5. Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Semua", "Materi", "Tugas", "Siswa")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BilindiBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 6. Post List
        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada postingan yang sesuai filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredPosts, key = { it.id }) { post ->
                val isOwnerOrAdmin = currentUser.id == post.authorId || currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL
                
                PostCardItem(
                    post = post,
                    author = users[post.authorId],
                    onLikeClick = { repository.toggleLikePost(post.id) },
                    onCommentClick = { activeCommentPost = post },
                    onShareClick = {
                        Toast.makeText(context, "Tautan postingan disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    onEditClick = if (isOwnerOrAdmin && post.type == PostType.LEARNING) {
                        {
                            postToEdit = post
                            showTeacherModal = true
                        }
                    } else null,
                    onHashtagClick = { tag ->
                        activeHashtagFilter = tag
                        onHashtagFilter(tag)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }

    // Teacher Create/Edit Modal
    if (showTeacherModal) {
        TeacherCreateModal(
            currentUser = currentUser,
            initialPost = postToEdit,
            onDismiss = {
                showTeacherModal = false
                postToEdit = null
            },
            onSubmitMateri = { title, targetClass, structured ->
                if (postToEdit != null) {
                    val updatedPost = postToEdit!!.copy(
                        title = title,
                        targetClass = targetClass,
                        structuredContent = structured
                    )
                    repository.updatePost(updatedPost)
                    Toast.makeText(context, "Materi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                } else {
                    val newPost = Post(
                        id = "p_" + System.currentTimeMillis(),
                        authorId = currentUser.id,
                        type = PostType.LEARNING,
                        status = PostStatus.APPROVED,
                        timestamp = "Baru saja",
                        title = title,
                        targetClass = targetClass,
                        structuredContent = structured,
                        likes = 0,
                        commentsCount = 0
                    )
                    repository.addPost(newPost)
                    Toast.makeText(context, "Modul pembelajaran berhasil dipublikasikan untuk $targetClass!", Toast.LENGTH_SHORT).show()
                }
            },
            onSubmitTugas = { title, desc, targetClass, deadline, hashtag, attachName ->
                val newPost = Post(
                    id = "p_" + System.currentTimeMillis(),
                    authorId = currentUser.id,
                    type = PostType.ASSIGNMENT,
                    status = PostStatus.APPROVED,
                    timestamp = "Baru saja",
                    title = "TUGAS: $title",
                    content = "📢 INSTRUKSI TUGAS:\n$desc\n\n⚠️ Cara Mengumpulkan:\nBuat postingan baru (Foto/Bukti Eksperimen) dan cantumkan hashtag di bawah ini agar otomatis terdata di buku nilai.",
                    hashtag = hashtag,
                    targetClass = targetClass,
                    assignmentDetail = AssignmentDetail(
                        deadline = deadline,
                        targetClass = targetClass,
                        attachmentName = attachName
                    ),
                    likes = 0,
                    commentsCount = 0
                )
                repository.addPost(newPost)
                Toast.makeText(context, "Tugas baru diterbitkan untuk kelas $targetClass!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Student / General Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            currentUser = currentUser,
            initialHashtag = activeHashtagFilter ?: "#TugasIPA7Gerhana",
            onDismiss = { showCreatePostDialog = false },
            onSubmitPost = { content, hashtag, imageUrl, targetClass ->
                val isStudent = currentUser.role == UserRole.STUDENT
                val newPost = Post(
                    id = "p_" + System.currentTimeMillis(),
                    authorId = currentUser.id,
                    type = PostType.STATUS,
                    status = if (isStudent) PostStatus.PENDING else PostStatus.APPROVED,
                    timestamp = "Baru saja",
                    content = content,
                    hashtag = hashtag,
                    imageUrl = imageUrl,
                    targetClass = if (isStudent) (currentUser.className ?: "VII-A") else (targetClass ?: "Semua Kelas"),
                    likes = 0,
                    commentsCount = 0
                )
                repository.addPost(newPost)
                if (isStudent) {
                    Toast.makeText(context, "Postingan masuk ke Moderasi Otomatis dan sedang ditinjau.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Postingan berhasil dibagikan!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Add Story Dialog
    if (showAddStoryDialog) {
        AddStoryDialog(
            currentUser = currentUser,
            onDismiss = { showAddStoryDialog = false },
            onAddStory = { url, cap, targetClass ->
                repository.addStory(url, cap, targetClass)
                Toast.makeText(context, "Cerita Anda berhasil diunggah!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Comments Bottom Sheet
    activeCommentPost?.let { post ->
        val postComments = comments.filter { it.postId == post.id }
        CommentSheet(
            post = post,
            comments = postComments,
            users = users,
            currentUser = currentUser,
            onDismiss = { activeCommentPost = null },
            onSendComment = { text ->
                repository.addComment(post.id, text)
            }
        )
    }
}

@Composable
private fun AddStoryDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onAddStory: (imageUrl: String, caption: String, targetClass: String?) -> Unit
) {
    val isStudent = currentUser.role == UserRole.STUDENT
    var targetClass by remember { mutableStateOf(if (isStudent) (currentUser.className ?: "VII-A") else "Semua Kelas") }
    var caption by remember { mutableStateOf("Aktivitas belajar hari ini ✨") }
    val sampleStoryImages = listOf(
        "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=400&q=80" to "Diskusi Belajar",
        "https://images.unsplash.com/photo-1577896851231-70ef18881754?auto=format&fit=crop&w=400&q=80" to "Presentasi Kelas",
        "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=400&q=80" to "Praktik Lab"
    )
    var selectedImage by remember { mutableStateOf(sampleStoryImages.first().first) }
    var isFromGallery by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImage = uri.toString()
            isFromGallery = true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Buat Cerita Baru",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isStudent) {
                            Text(
                                text = "🔒 Terkunci Kelas: ${currentUser.className ?: "VII-A"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BilindiBlue
                            )
                        } else {
                            Text(
                                text = "Target: $targetClass",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                if (!isStudent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Target Kelas Cerita:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                // Button to pick image from device gallery
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFromGallery) "Ganti Foto dari Galeri HP" else "Ambil Foto dari Galeri HP",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preview of Selected Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    coil.compose.AsyncImage(
                        model = selectedImage,
                        contentDescription = "Preview Cerita",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (isFromGallery) "📷 Dari Galeri HP" else "📚 Template Sekolah",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Atau pilih foto aktivitas sekolah:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sampleStoryImages) { (url, label) ->
                        val isSelected = selectedImage == url && !isFromGallery
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BilindiBlue) else null,
                            modifier = Modifier
                                .width(70.dp)
                                .height(60.dp)
                                .clickable {
                                    selectedImage = url
                                    isFromGallery = false
                                }
                        ) {
                            coil.compose.AsyncImage(
                                model = url,
                                contentDescription = label,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Keterangan Cerita (Opsional)") },
                    placeholder = { Text("Tulis kegiatan belajarmu hari ini...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onAddStory(selectedImage, caption, targetClass)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text("Bagikan ke Cerita", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
