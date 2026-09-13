package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun PostCardItem(
    post: Post,
    author: User?,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onHashtagClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activePdfPreview by remember { mutableStateOf<Pair<String, String>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}")
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {

            // Pending Moderation Banner
            if (post.status == PostStatus.PENDING) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Menunggu Moderasi: Postingan Anda sedang ditinjau sebelum dipublikasikan.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Author Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        AsyncImage(
                            model = author?.avatarUrl ?: "",
                            contentDescription = author?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = author?.name ?: "Pengguna",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            // Type pill
                            when (post.type) {
                                PostType.LEARNING -> {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = BilindiBlueLight,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Materi",
                                            color = BilindiBlue,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                PostType.ASSIGNMENT -> {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "Tugas",
                                            color = Color(0xFFDC2626),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                else -> {}
                            }

                            // Class Target Pill
                            val displayClass = post.targetClass ?: post.assignmentDetail?.targetClass
                            if (!displayClass.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (displayClass == "Semua Kelas") Color(0xFFF3E8FF) else Color(0xFFE0F2FE),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (displayClass == "Semua Kelas") "Semua Kelas" else "Kelas $displayClass",
                                        color = if (displayClass == "Semua Kelas") Color(0xFF7E22CE) else Color(0xFF0284C7),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${post.timestamp} • ${
                                if (author?.role == UserRole.TEACHER) author.subject ?: "Guru"
                                else if (author?.role == UserRole.PRINCIPAL) "Kepala Sekolah"
                                else "Kelas ${author?.className ?: "Siswa"}"
                            }",
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                var expandedMenu by remember { mutableStateOf(false) }
                // Only show menu if user is author or has elevated role (Teacher/Principal)
                // In a real app we would pass currentUser here, but we can assume if onEditClick is provided, we can show it
                if (onEditClick != null) {
                    Box {
                        IconButton(onClick = { expandedMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opsi",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Kiriman") },
                                onClick = {
                                    expandedMenu = false
                                    onEditClick.invoke()
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Title
            if (!post.title.isNullOrEmpty()) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Structured Learning Content (4 Segments from curriculum)
            if (post.type == PostType.LEARNING && post.structuredContent != null) {
                val sc = post.structuredContent
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header / Judul Media Banner (if attached)
                    if (!sc.headerSection.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = sc.headerSection.imageUrl,
                            contentDescription = "Banner Materi Modul",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BilindiBlue.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        )
                    }

                    if (!sc.headerSection.videoUrl.isNullOrEmpty()) {
                        SectionMediaVideoCard(
                            videoUrl = sc.headerSection.videoUrl!!,
                            label = "Video Pengantar Modul",
                            onOpen = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sc.headerSection.videoUrl)))
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    if (!sc.headerSection.embedUrl.isNullOrEmpty()) {
                        SectionMediaEmbedCard(
                            embedUrl = sc.headerSection.embedUrl!!,
                            label = "Embed / Lab Maya Pengantar",
                            onOpen = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sc.headerSection.embedUrl)))
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    if (!sc.headerSection.pdfName.isNullOrEmpty()) {
                        SectionMediaPdfCard(
                            pdfName = sc.headerSection.pdfName!!,
                            label = "Dokumen Modul Ajar Lengkap (PDF)",
                            onPreview = {
                                activePdfPreview = Pair(post.title ?: "Modul Ajar", sc.headerSection.pdfName!!)
                            }
                        )
                    }

                    // 1. Pemantik with rich media
                    StructuredSectionBox(
                        title = "💡 Pertanyaan Pemantik",
                        sectionData = sc.pemantik,
                        borderColor = LearningYellow,
                        containerColor = LearningYellow.copy(alpha = 0.08f),
                        onPreviewPdf = { docTitle, fileName ->
                            activePdfPreview = Pair(docTitle, fileName)
                        }
                    )

                    // 2. Tujuan with rich media
                    StructuredSectionBox(
                        title = "🎯 Tujuan Pembelajaran",
                        sectionData = sc.tujuan,
                        borderColor = LearningBlue,
                        containerColor = LearningBlue.copy(alpha = 0.08f),
                        onPreviewPdf = { docTitle, fileName ->
                            activePdfPreview = Pair(docTitle, fileName)
                        }
                    )

                    // 3. Inti & Interactive Lab with rich media
                    StructuredSectionBox(
                        title = "📚 Materi Inti & Eksplorasi",
                        sectionData = sc.inti,
                        borderColor = LearningGreen,
                        containerColor = LearningGreen.copy(alpha = 0.08f),
                        onPreviewPdf = { docTitle, fileName ->
                            activePdfPreview = Pair(docTitle, fileName)
                        }
                    )

                    // 4. Asesmen with rich media
                    StructuredSectionBox(
                        title = "📝 Asesmen / LKPD Siswa",
                        sectionData = sc.asesmen,
                        borderColor = LearningRed,
                        containerColor = LearningRed.copy(alpha = 0.08f),
                        onPreviewPdf = { docTitle, fileName ->
                            activePdfPreview = Pair(docTitle, fileName)
                        }
                    )
                }
            } else {
                // Regular Post or Assignment Content
                if (!post.content.isNullOrEmpty()) {
                    Text(
                        text = post.content,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Hashtag display
            if (!post.hashtag.isNullOrEmpty()) {
                Text(
                    text = post.hashtag,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BilindiBlue,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onHashtagClick?.invoke(post.hashtag) }
                )
            }

            // Assignment Attachment Preview (if any)
            if (post.type == PostType.ASSIGNMENT && post.assignmentDetail != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = post.assignmentDetail.attachmentName ?: "Dokumen Panduan Tugas",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Batas Waktu: ${post.assignmentDetail.deadline} • Target: ${post.assignmentDetail.targetClass}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        post.assignmentDetail.attachmentName?.let { attachName ->
                            Button(
                                onClick = {
                                    activePdfPreview = Pair(post.title ?: "Panduan Penugasan", attachName)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Post Image (Photo evidence of homework, eclipse, etc.)
            if (!post.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Gambar Postingan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Likes & Comments Count summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(BilindiBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${post.likes}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${post.commentsCount} komentar",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onCommentClick)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            // Interaction Buttons Row: Like, Comment, Share
            val likeColor by animateColorAsState(
                targetValue = if (post.isLikedByMe) BilindiBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "likeColor"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f).testTag("like_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Suka",
                        tint = likeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (post.isLikedByMe) "Disukai" else "Suka",
                        color = likeColor,
                        fontWeight = if (post.isLikedByMe) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f).testTag("comment_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Komentar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Komentar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(
                    onClick = onShareClick,
                    modifier = Modifier.weight(1f).testTag("share_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Bagikan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bagikan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    if (activePdfPreview != null) {
        PdfPreviewDialog(
            documentTitle = activePdfPreview!!.first,
            fileName = activePdfPreview!!.second,
            onDismiss = { activePdfPreview = null }
        )
    }
}

@Composable
private fun StructuredSectionBox(
    title: String,
    sectionData: SectionData,
    borderColor: Color,
    containerColor: Color,
    onPreviewPdf: (docTitle: String, fileName: String) -> Unit
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Colored left stripe
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = borderColor
                )
                if (sectionData.text.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sectionData.text,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }

                // 1. Gambar section (if attached)
                if (!sectionData.imageUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = sectionData.imageUrl,
                        contentDescription = "Gambar $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, borderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    )
                }

                // 2. Video section (if attached)
                if (!sectionData.videoUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionMediaVideoCard(
                        videoUrl = sectionData.videoUrl!!,
                        label = "Video Pembelajaran: $title",
                        onOpen = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sectionData.videoUrl)))
                            } catch (_: Exception) {}
                        }
                    )
                }

                // 3. Embed section (if attached)
                if (!sectionData.embedUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionMediaEmbedCard(
                        embedUrl = sectionData.embedUrl!!,
                        label = sectionData.fileName ?: "Simulasi PhET / Web Interaktif",
                        onOpen = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sectionData.embedUrl)))
                            } catch (_: Exception) {}
                        }
                    )
                }

                // 4. PDF section (if attached)
                if (!sectionData.pdfName.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionMediaPdfCard(
                        pdfName = sectionData.pdfName!!,
                        label = "Lampiran Dokumen PDF",
                        onPreview = {
                            onPreviewPdf(title, sectionData.pdfName!!)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionMediaVideoCard(
    videoUrl: String,
    label: String,
    onOpen: () -> Unit
) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFFF1F2),
        border = androidx.compose.foundation.BorderStroke(1.dp, LearningRed.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = LearningRed,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Putar Video",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LearningRed
                )
                Text(
                    text = "Tonton Video (YouTube / MP4) • Klik untuk Membuka",
                    fontSize = 10.sp,
                    color = Color.DarkGray
                )
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                tint = LearningRed,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun SectionMediaEmbedCard(
    embedUrl: String,
    label: String,
    onOpen: () -> Unit
) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(6.dp),
        color = BilindiBlueLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, BilindiBlue.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = BilindiBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BilindiBlue
                )
                Text(
                    text = "Buka Laboratorium Maya / Simulasi Interaktif PhET",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                tint = BilindiBlue,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun SectionMediaPdfCard(
    pdfName: String,
    label: String,
    onPreview: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFFFBEB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = LearningRed,
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pdfName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    maxLines = 1
                )
                Text(
                    text = "$label • Dokumen PDF Terverifikasi",
                    fontSize = 10.sp,
                    color = Color.DarkGray
                )
            }

            Button(
                onClick = onPreview,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LearningRed),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Preview PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
