package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import coil.compose.AsyncImage
import com.example.model.User
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentUser: User,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onMessageClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSwitchUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_nav_bar"),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // BilindiWall Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onSearchChange("") }
                        .weight(1f, fill = false)
                        .padding(end = 4.dp)
                ) {
                    BilindiLogoIcon(size = 36.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Bilindi",
                            color = BilindiBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = "Wall",
                            color = com.example.ui.theme.LearningGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Right Actions: Dark mode toggle, Search, Notifications, Role pill & Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isDarkMode by com.example.data.BilindiWallRepository.instance.isDarkMode.collectAsState()
                    IconButton(
                        onClick = { com.example.data.BilindiWallRepository.instance.toggleDarkMode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("dark_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Mode Terang" else "Mode Gelap",
                            tint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("notification_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Red badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                    }

                    // Messenger / Chat Button
                    val directMessages by com.example.data.BilindiWallRepository.instance.directMessages.collectAsState()
                    val unreadCount = remember(directMessages, currentUser.id) {
                        directMessages.count { it.recipientId == currentUser.id && !it.isRead }
                    }

                    Box {
                        IconButton(
                            onClick = onMessageClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("messenger_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Messenger Pesan",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Role pill button (quick switch role)
                    Surface(
                        onClick = onSwitchUserClick,
                        shape = RoundedCornerShape(16.dp),
                        color = BilindiBlueLight,
                        modifier = Modifier.testTag("role_switch_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentUser.role.label,
                                color = BilindiBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Ganti Akun",
                                tint = BilindiBlue,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(start = 2.dp)
                            )
                        }
                    }

                    // Avatar button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, BilindiBlue, CircleShape)
                            .clickable(onClick = onProfileClick)
                            .testTag("current_user_avatar")
                    ) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Expandable Search Field
            if (isSearchExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Cari materi, tugas, #hashtag, atau siswa...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Bersihkan",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("search_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }
    }
}
