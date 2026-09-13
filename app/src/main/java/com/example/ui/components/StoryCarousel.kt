package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.Story
import com.example.model.User
import com.example.ui.theme.BilindiBlue

@Composable
fun StoryCarousel(
    currentUser: User,
    stories: List<Story>,
    users: Map<String, User>,
    onAddStoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("story_carousel"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Create Story Card
        item {
            Card(
                modifier = Modifier
                    .width(104.dp)
                    .height(164.dp)
                    .clickable(onClick = onAddStoryClick)
                    .testTag("create_story_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top half: User Avatar
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )
                    // Plus button overlapping border
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 14.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BilindiBlue)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Cerita",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Bottom label
                    Text(
                        text = "Buat Cerita",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                    )
                }
            }
        }

        // Stories of classmates and teachers
        items(stories) { story ->
            val author = users[story.authorId]
            Card(
                modifier = Modifier
                    .width(104.dp)
                    .height(164.dp)
                    .clickable { selectedStory = story }
                    .testTag("story_card_${story.id}"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = story.imageUrl,
                        contentDescription = story.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    // Author circular avatar with ring
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, BilindiBlue, CircleShape)
                    ) {
                        AsyncImage(
                            model = author?.avatarUrl ?: "",
                            contentDescription = author?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Author name text
                    Text(
                        text = author?.name?.split(" ")?.firstOrNull() ?: "Teman",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Story Fullscreen Viewer Dialog
    selectedStory?.let { story ->
        val author = users[story.authorId]
        var replyText by remember { mutableStateOf("") }
        var isReplySent by remember { mutableStateOf(false) }

        Dialog(
            onDismissRequest = { selectedStory = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = story.imageUrl,
                    contentDescription = story.caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Top story author bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, BilindiBlue, CircleShape)
                    ) {
                        AsyncImage(
                            model = author?.avatarUrl ?: "",
                            contentDescription = author?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = author?.name ?: "Pengguna",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${story.timestamp} • ${author?.role?.label ?: ""}${if (!author?.className.isNullOrBlank()) " • ${author?.className}" else ""} • 🎯 ${story.targetClass ?: "Semua Kelas"}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(
                        onClick = { selectedStory = null },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White
                        )
                    }
                }

                // Bottom Story Details & Reply
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    if (story.caption.isNotEmpty()) {
                        Text(
                            text = story.caption,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Quick emoji reactions row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("❤️", "👏", "🔥", "💡", "📚", "✨").forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        isReplySent = true
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    if (isReplySent) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BilindiBlue.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "✓ Balasan terkirim ke ${author?.name ?: "pembuat cerita"}!",
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                placeholder = { Text("Kirim balasan ke cerita...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = BilindiBlue
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (replyText.isNotBlank()) {
                                        isReplySent = true
                                        replyText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BilindiBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Kirim",
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
}
