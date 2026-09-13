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
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.*
import com.example.ui.components.ClassFilterBar
import com.example.ui.components.GradeDialog
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@Composable
fun TeacherDashboardScreen(
    repository: BilindiWallRepository,
    currentUser: User,
    onNavigateToFeedWithHashtag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by repository.posts.collectAsState()
    val submissions by repository.submissions.collectAsState()
    val users by repository.users.collectAsState()

    var activeGradeSubmission by remember { mutableStateOf<Submission?>(null) }

    val assignmentPosts = posts.filter { it.type == PostType.ASSIGNMENT }
    val isTeacherOrAdmin = currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL
    val selectedClassFilter by repository.selectedClassFilter.collectAsState()

    val displayAssignments = remember(assignmentPosts, selectedClassFilter, currentUser) {
        if (currentUser.role == UserRole.STUDENT) {
            val studentClass = currentUser.className ?: "VII-A"
            assignmentPosts.filter {
                it.targetClass == null || it.targetClass == "Semua Kelas" || it.targetClass.equals(studentClass, ignoreCase = true)
            }
        } else {
            if (selectedClassFilter == "Semua Kelas" || selectedClassFilter.isBlank()) {
                assignmentPosts
            } else {
                assignmentPosts.filter {
                    it.targetClass == null || it.targetClass == "Semua Kelas" || it.targetClass.equals(selectedClassFilter, ignoreCase = true)
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_dashboard_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BilindiBlueLight,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isTeacherOrAdmin) Icons.Default.Checklist else Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = BilindiBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isTeacherOrAdmin) "Dashboard Pengumpulan Tugas" else "Daftar Tugas & Hasil Penilaian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isTeacherOrAdmin) "Pantau pengumpulan karya siswa berbasis hashtag otomatis" else "Cek tugas aktif kelasmu dan nilai masukan dari Bapak/Ibu guru",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Stats Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val relevantSubmissions = submissions.filter { sub ->
                    displayAssignments.any { it.hashtag.equals(sub.assignmentHashtag, ignoreCase = true) }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${displayAssignments.size}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BilindiBlue)
                        Text(text = "Tugas Aktif", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${relevantSubmissions.size}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFFD97706))
                        Text(text = "Terkumpul", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${relevantSubmissions.count { it.isGraded }}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = LearningGreen)
                        Text(text = "Dinilai", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Class Filter Bar
        item {
            val availableClasses by repository.availableClasses.collectAsState()
            ClassFilterBar(
                currentUser = currentUser,
                selectedClass = selectedClassFilter,
                availableClasses = availableClasses,
                onClassSelected = { repository.setSelectedClassFilter(it) }
            )
        }

        if (displayAssignments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentUser.role == UserRole.STUDENT) "Belum ada penugasan untuk kelas ${currentUser.className ?: "VII-A"}." else "Belum ada penugasan pada filter kelas ini.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(displayAssignments) { assignment ->
                val matchingSubmissions = submissions.filter {
                    it.assignmentHashtag.equals(assignment.hashtag, ignoreCase = true)
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth().testTag("assignment_panel_${assignment.id}")
                ) {
                    Column {
                        // Assignment Title and Hashtag info bar
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = assignment.title ?: "Tugas Kelas",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Tagar Pengumpulan: ",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Surface(
                                            color = BilindiBlueLight,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.clickable {
                                                assignment.hashtag?.let { onNavigateToFeedWithHashtag(it) }
                                            }
                                        ) {
                                            Text(
                                                text = assignment.hashtag ?: "",
                                                color = BilindiBlue,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "🎯 ${assignment.targetClass ?: "Semua Kelas"}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "${matchingSubmissions.size} Terkumpul",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Submissions table / list
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (matchingSubmissions.isEmpty()) {
                                Text(
                                    text = "Belum ada siswa yang mengumpulkan tugas dengan hashtag ini.",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            } else {
                                matchingSubmissions.forEach { sub ->
                                    val student = users[sub.studentId]
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                            ) {
                                                AsyncImage(
                                                    model = student?.avatarUrl ?: "",
                                                    contentDescription = student?.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = student?.name ?: "Siswa",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "${sub.timestamp} • ${student?.className ?: ""}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                if (sub.isGraded) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(top = 2.dp)
                                                    ) {
                                                        Surface(
                                                            color = Color(0xFFD1FAE5),
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text(
                                                                text = "Nilai: ${sub.grade}/100",
                                                                color = LearningGreen,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                        if (!sub.feedback.isNullOrEmpty()) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "\"${sub.feedback}\"",
                                                                fontSize = 11.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Surface(
                                                        color = Color(0xFFFEF3C7),
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.padding(top = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Menunggu Penilaian",
                                                            color = Color(0xFF92400E),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // Teacher Action: Beri Nilai
                                            if (isTeacherOrAdmin) {
                                                Button(
                                                    onClick = { activeGradeSubmission = sub },
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (sub.isGraded) BilindiBlueLight else BilindiBlue,
                                                        contentColor = if (sub.isGraded) BilindiBlue else Color.White
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("grade_btn_${sub.id}")
                                                ) {
                                                    Text(
                                                        text = if (sub.isGraded) "Ubah Nilai" else "Beri Nilai",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
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
            }
        }
    }

    // Grade Dialog
    activeGradeSubmission?.let { sub ->
        GradeDialog(
            submission = sub,
            student = users[sub.studentId],
            onDismiss = { activeGradeSubmission = null },
            onSaveGrade = { grade, feedback ->
                repository.gradeSubmission(sub.id, grade, feedback)
                Toast.makeText(context, "Nilai berhasil disimpan untuk ${users[sub.studentId]?.name}!", Toast.LENGTH_SHORT).show()
                activeGradeSubmission = null
            }
        )
    }
}
