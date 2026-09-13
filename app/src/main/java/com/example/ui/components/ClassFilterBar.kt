package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassFilterBar(
    currentUser: User,
    selectedClass: String,
    availableClasses: List<String>,
    onClassSelected: (String) -> Unit,
    onAddClass: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showAddClassDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (currentUser.role == UserRole.STUDENT) {
        // LOCKED FOR STUDENT: Clear, reassuring locked indicator
        val studentClass = currentUser.className ?: "VII-A"
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BilindiBlue.copy(alpha = 0.35f)),
            modifier = modifier
                .fillMaxWidth()
                .testTag("student_locked_class_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BilindiBlueLight,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = BilindiBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Ruang Kelas: $studentClass",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LearningGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "🔒 Terkunci",
                                color = LearningGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Beranda & materi dikhususkan untuk teman sekelasmu agar tidak bingung.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    } else {
        // FOR TEACHERS / PRINCIPALS: Flexible switcher
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = modifier
                .fillMaxWidth()
                .testTag("teacher_class_filter_bar")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = BilindiBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filter Kelas Guru:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BilindiBlueLight
                    ) {
                        Text(
                            text = if (selectedClass == "Semua Kelas") "Tampil: Semua Kelas" else "Tampil: Kelas $selectedClass",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BilindiBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable chips for classes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    availableClasses.forEach { cls ->
                        val isSelected = selectedClass == cls
                        FilterChip(
                            selected = isSelected,
                            onClick = { onClassSelected(cls) },
                            label = {
                                Text(
                                    text = cls,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                if (cls == "Semua Kelas") {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BilindiBlue,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    if (onAddClass != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .height(32.dp)
                                .clickable { showAddClassDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Kelas",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tambah",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddClassDialog && onAddClass != null) {
        var newClassName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddClassDialog = false },
            title = { Text("Tambah Kelas Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newClassName,
                    onValueChange = { newClassName = it },
                    label = { Text("Nama Kelas") },
                    placeholder = { Text("cth: IX-A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newClassName.isNotBlank()) {
                            onAddClass(newClassName.trim())
                        }
                        showAddClassDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddClassDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
