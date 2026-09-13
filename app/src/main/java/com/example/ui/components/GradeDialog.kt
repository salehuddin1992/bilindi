package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Submission
import com.example.model.User
import com.example.ui.theme.BilindiBlue

@Composable
fun GradeDialog(
    submission: Submission,
    student: User?,
    onDismiss: () -> Unit,
    onSaveGrade: (grade: Int, feedback: String) -> Unit
) {
    var gradeText by remember { mutableStateOf(submission.grade?.toString() ?: "90") }
    var feedbackText by remember { mutableStateOf(submission.feedback ?: "Pekerjaan rapi dan sesuai dengan instruksi modul.") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("grade_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Grade, contentDescription = null, tint = BilindiBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Penilaian Tugas Siswa",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Student overview
                Text(
                    text = "Siswa: ${student?.name ?: "Siswa"} (${student?.className ?: ""})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Tagar Tugas: ${submission.assignmentHashtag}",
                    fontSize = 12.sp,
                    color = BilindiBlue,
                    fontWeight = FontWeight.Bold
                )
                if (submission.content.isNotEmpty()) {
                    Text(
                        text = "\"${submission.content}\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score input
                OutlinedTextField(
                    value = gradeText,
                    onValueChange = {
                        gradeText = it
                        errorMessage = null
                    },
                    label = { Text("Nilai (0 - 100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Feedback notes
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Catatan / Masukan Guru") },
                    placeholder = { Text("Berikan apresiasi dan masukan konstruktif...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        val score = gradeText.toIntOrNull()
                        if (score == null || score < 0 || score > 100) {
                            errorMessage = "Masukkan nilai valid antara 0 dan 100"
                        } else {
                            onSaveGrade(score, feedbackText)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_grade_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text("Simpan Nilai & Publikasikan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
