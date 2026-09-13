package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.DirectMessage
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerScreen(
    repository: BilindiWallRepository,
    currentUser: User,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usersMap by repository.users.collectAsState()
    val allMessages by repository.directMessages.collectAsState()

    var activeChatUser by remember { mutableStateOf<User?>(null) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }

    // Group messages into conversations with contacts
    val contactIds = remember(allMessages, currentUser.id) {
        val ids = mutableSetOf<String>()
        allMessages.forEach { msg ->
            if (msg.senderId == currentUser.id) ids.add(msg.recipientId)
            if (msg.recipientId == currentUser.id) ids.add(msg.senderId)
        }
        ids
    }

    if (activeChatUser != null) {
        val targetUser = activeChatUser!!
        val conversationMessages = allMessages.filter {
            (it.senderId == currentUser.id && it.recipientId == targetUser.id) ||
            (it.senderId == targetUser.id && it.recipientId == currentUser.id)
        }

        ChatConversationView(
            currentUser = currentUser,
            recipient = targetUser,
            messages = conversationMessages,
            onBack = { activeChatUser = null },
            onSendMessage = { text, imageUrl ->
                repository.sendDirectMessage(
                    senderId = currentUser.id,
                    recipientId = targetUser.id,
                    text = text,
                    imageUrl = imageUrl
                )
            }
        )
    } else {
        // Conversation List Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("messenger_screen")
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BilindiBlue,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Messenger BilindiWall",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Pesan & Diskusi Siswa • Guru",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showNewChatDialog = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BilindiBlueLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Pesan Baru",
                            tint = BilindiBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Search and Role Filter Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama guru atau teman...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = BilindiBlue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedRoleFilter == null,
                        onClick = { selectedRoleFilter = null },
                        label = { Text("Semua Chat", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedRoleFilter == UserRole.TEACHER,
                        onClick = {
                            selectedRoleFilter = if (selectedRoleFilter == UserRole.TEACHER) null else UserRole.TEACHER
                        },
                        label = { Text("Guru", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedRoleFilter == UserRole.STUDENT,
                        onClick = {
                            selectedRoleFilter = if (selectedRoleFilter == UserRole.STUDENT) null else UserRole.STUDENT
                        },
                        label = { Text("Siswa", fontSize = 11.sp) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // Contact list / conversation list
            val filteredContacts = remember(usersMap, contactIds, searchQuery, selectedRoleFilter, currentUser.id) {
                val otherUsers = usersMap.values.filter { it.id != currentUser.id }
                otherUsers.filter { user ->
                    val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
                    val matchesSearch = searchQuery.isBlank() ||
                            user.name.contains(searchQuery, ignoreCase = true) ||
                            (user.subject?.contains(searchQuery, ignoreCase = true) == true) ||
                            (user.className?.contains(searchQuery, ignoreCase = true) == true)
                    matchesRole && matchesSearch
                }.sortedByDescending { user ->
                    // Prioritize users with active conversation
                    if (contactIds.contains(user.id)) 1 else 0
                }
            }

            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ada obrolan yang cocok",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Coba ubah kata kunci pencarian atau mulai pesan baru dengan guru / siswa.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { contact ->
                        val lastMsg = allMessages.filter {
                            (it.senderId == currentUser.id && it.recipientId == contact.id) ||
                            (it.senderId == contact.id && it.recipientId == currentUser.id)
                        }.lastOrNull()

                        val isUnread = lastMsg != null && lastMsg.senderId == contact.id && !lastMsg.isRead

                        ConversationItem(
                            contact = contact,
                            lastMessage = lastMsg?.text ?: "Ketuk untuk memulai obrolan dengan ${contact.name}",
                            timestamp = lastMsg?.timestamp ?: "",
                            isUnread = isUnread,
                            onClick = { activeChatUser = contact }
                        )
                    }
                }
            }
        }
    }

    if (showNewChatDialog) {
        NewChatSelectDialog(
            currentUser = currentUser,
            allUsers = usersMap.values.filter { it.id != currentUser.id },
            onSelectUser = { targetUser ->
                showNewChatDialog = false
                activeChatUser = targetUser
            },
            onDismiss = { showNewChatDialog = false }
        )
    }
}

@Composable
private fun ConversationItem(
    contact: User,
    lastMessage: String,
    timestamp: String,
    isUnread: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = contact.avatarUrl,
                    contentDescription = contact.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (contact.role == UserRole.TEACHER) 2.dp else 1.dp,
                            color = if (contact.role == UserRole.TEACHER) LearningGreen else BilindiBlue,
                            shape = CircleShape
                        )
                )
                // Online green dot indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RoleBadgePill(role = contact.role, detail = contact.subject ?: contact.className)
                    }

                    if (timestamp.isNotEmpty()) {
                        Text(
                            text = timestamp,
                            fontSize = 11.sp,
                            color = if (isUnread) BilindiBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = lastMessage,
                        fontSize = 12.sp,
                        color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BilindiBlue)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadgePill(role: UserRole, detail: String?) {
    val bg = when (role) {
        UserRole.TEACHER -> LearningGreen.copy(alpha = 0.15f)
        UserRole.PRINCIPAL -> Color(0xFFE0E7FF)
        UserRole.STUDENT -> BilindiBlueLight
    }
    val fg = when (role) {
        UserRole.TEACHER -> LearningGreen
        UserRole.PRINCIPAL -> Color(0xFF4338CA)
        UserRole.STUDENT -> BilindiBlue
    }

    val label = when (role) {
        UserRole.TEACHER -> "Guru"
        UserRole.PRINCIPAL -> "Kepsek"
        UserRole.STUDENT -> "Siswa"
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bg
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatConversationView(
    currentUser: User,
    recipient: User,
    messages: List<DirectMessage>,
    onBack: () -> Unit,
    onSendMessage: (String, String?) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSendMessage("", uri.toString())
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("chat_conversation_view")
    ) {
        // Chat Header
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        AsyncImage(
                            model = recipient.avatarUrl,
                            contentDescription = recipient.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, BilindiBlue, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = recipient.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RoleBadgePill(role = recipient.role, detail = recipient.subject ?: recipient.className)
                        }
                        Text(
                            text = if (recipient.role == UserRole.TEACHER) "Guru Pengajar • Aktif Sekarang" else "Siswa • Aktif Sekarang",
                            fontSize = 11.sp,
                            color = Color(0xFF22C55E)
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Info Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = recipient.avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.dp, BilindiBlue, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = recipient.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (recipient.role == UserRole.TEACHER)
                            "Guru Mata Pelajaran: ${recipient.subject ?: "Semua Mapel"}"
                        else
                            "Siswa Kelas: ${recipient.className ?: "SMP Negeri sinombayuga"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "🔒 Pesan terenkripsi untuk lingkungan belajar SMP Negeri sinombayuga",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUser.id
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    if (!isMe) {
                        AsyncImage(
                            model = recipient.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .align(Alignment.Bottom)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            color = if (isMe) BilindiBlue else MaterialTheme.colorScheme.surface,
                            border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                            shadowElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    if (!msg.imageUrl.isNullOrBlank()) 6.dp else 12.dp,
                                    if (!msg.imageUrl.isNullOrBlank()) 6.dp else 8.dp
                                )
                            ) {
                                if (!msg.imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = msg.imageUrl,
                                        contentDescription = "Foto Obrolan",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 200.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    if (msg.text.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                                if (msg.text.isNotBlank()) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.5.sp,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = if (!msg.imageUrl.isNullOrBlank()) Modifier.padding(horizontal = 6.dp, vertical = 2.dp) else Modifier
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = msg.timestamp,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Quick suggestions for easy chatting
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = if (recipient.role == UserRole.TEACHER) {
                listOf(
                    "Pak/Bu, izin bertanya materi tugas...",
                    "Apakah tugas saya sudah diperiksa?",
                    "Terima kasih atas bimbingannya!",
                    "Siap, akan saya kerjakan segera."
                )
            } else {
                listOf(
                    "Halo, apakah sudah selesai tugas tadi?",
                    "Ayo belajar kelompok bareng!",
                    "Bisa tolong jelaskan materi ini?",
                    "Siap, terima kasih ya!"
                )
            }
            items(suggestions) { hint ->
                Surface(
                    onClick = { textInput = hint },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = hint,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Photo Attachment Button
                IconButton(
                    onClick = {
                        photoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Lampirkan Foto",
                        tint = BilindiBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = if (recipient.role == UserRole.TEACHER) "Kirim pesan ke guru..." else "Kirim pesan ke teman...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        focusedBorderColor = BilindiBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(textInput.trim(), null)
                            textInput = ""
                        }
                    },
                    enabled = textInput.isNotBlank(),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (textInput.isNotBlank()) BilindiBlue else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = if (textInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NewChatSelectDialog(
    currentUser: User,
    allUsers: List<User>,
    onSelectUser: (User) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val eligibleUsers = remember(allUsers, currentUser) {
        if (currentUser.role == UserRole.STUDENT) {
            val myClass = currentUser.className ?: "VII-A"
            allUsers.filter {
                it.id != currentUser.id && (
                    it.role == UserRole.TEACHER ||
                    it.role == UserRole.PRINCIPAL ||
                    (it.role == UserRole.STUDENT && it.className == myClass)
                )
            }
        } else {
            allUsers.filter { it.id != currentUser.id }
        }
    }

    val filtered = eligibleUsers.filter {
        search.isBlank() || it.name.contains(search, ignoreCase = true) ||
        (it.subject?.contains(search, ignoreCase = true) == true) ||
        (it.className?.contains(search, ignoreCase = true) == true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mulai Obrolan Baru",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (currentUser.role == UserRole.STUDENT) {
                            Text(
                                text = "🔒 Khusus Teman Sekelas & Dewan Guru",
                                fontSize = 11.sp,
                                color = BilindiBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Cari nama guru atau siswa...", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { user ->
                        Surface(
                            onClick = { onSelectUser(user) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = user.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        RoleBadgePill(role = user.role, detail = user.subject ?: user.className)
                                    }
                                    Text(
                                        text = user.subject ?: user.className ?: "SMP Negeri sinombayuga",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
