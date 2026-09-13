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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.components.BilindiLogoFull
import com.example.ui.components.BilindiLogoIcon
import com.example.ui.theme.BilindiBlue
import com.example.ui.theme.BilindiBlueLight
import com.example.ui.theme.LearningGreen

@Composable
fun LoginScreen(
    users: Map<String, User>,
    onLogin: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // BilindiWall Official Brand Logo
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp)),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BilindiLogoIcon(size = 80.dp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Wordmark
            BilindiLogoFull(
                iconSize = 0.dp,
                textSize = 36
            )

            Text(
                text = "Belajar Interaktif Digital\nBersama SMP Negeri sinombayuga",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Box Card (Modern crisp container)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Masuk ke Akun Anda",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Error message banner
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFF991B1B),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Username field
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = {
                            usernameInput = it
                            errorMessage = null
                        },
                        label = { Text("Username / Nama Pengguna") },
                        placeholder = { Text("cth: guru, siswa, atau username Anda") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Password field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        label = { Text("Kata Sandi (Password)") },
                        placeholder = { Text("Masukkan kata sandi") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // Tombol Masuk / Login
                    Button(
                        onClick = {
                            if (usernameInput.isBlank()) {
                                errorMessage = "Silakan masukkan username Anda."
                                return@Button
                            }
                            if (passwordInput.isBlank()) {
                                errorMessage = "Silakan masukkan kata sandi Anda."
                                return@Button
                            }
                            val authenticated = BilindiWallRepository.instance.authenticate(usernameInput, passwordInput)
                            if (authenticated != null) {
                                Toast.makeText(context, "Selamat datang kembali, ${authenticated.name}!", Toast.LENGTH_SHORT).show()
                                onLogin(authenticated)
                            } else {
                                errorMessage = "Username atau kata sandi tidak cocok. Silakan periksa kembali atau lakukan registrasi akun baru."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                    ) {
                        Text(
                            text = "Masuk (Login)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tombol Login dengan Google
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Membuka halaman Google Login... (Simulasi)", Toast.LENGTH_SHORT).show()
                            // Simulate successful Google Login using demo account after short delay
                            // In a real app this would trigger Google SignIn Client
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("google_login_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email, // Representing Google/Email
                            contentDescription = "Logo Google",
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFFEA4335) // Google Red accent
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Masuk dengan Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Tombol Registrasi Akun Baru di Depan
                    Button(
                        onClick = { showRegisterDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("register_menu_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LearningGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Buat Akun Baru / Registrasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "© 2026 TIM IT SMP Negeri sinombayuga",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showRegisterDialog) {
        RegistrationDialog(
            onDismiss = { showRegisterDialog = false },
            onRegistered = { newUser ->
                showRegisterDialog = false
                usernameInput = newUser.username
                passwordInput = newUser.password
                onLogin(newUser)
            }
        )
    }
}

@Composable
private fun RegistrationDialog(
    onDismiss: () -> Unit,
    onRegistered: (User) -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var classNameOrSubject by remember { mutableStateOf("") }
    var nipOrNisn by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = LearningGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Registrasi Siswa & Guru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Lengkapi data untuk membuat akun baru di SMP Negeri sinombayuga.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Avatar preview & upload button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.5.dp, BilindiBlue, CircleShape)
                    ) {
                        if (!avatarUri.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pilih Foto Profil (Galeri)", fontSize = 12.sp)
                    }
                }

                // Peran / Role Selector
                Text(
                    text = "Pilih Peran Akun:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRole == UserRole.STUDENT,
                        onClick = { selectedRole = UserRole.STUDENT },
                        label = { Text("Siswa") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedRole == UserRole.TEACHER,
                        onClick = { selectedRole = UserRole.TEACHER },
                        label = { Text("Guru") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap *") },
                    placeholder = { Text(if (selectedRole == UserRole.TEACHER) "cth: Rahmawati, S.Pd" else "cth: Rizky Pratama") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Username login
                OutlinedTextField(
                    value = regUsername,
                    onValueChange = { regUsername = it },
                    label = { Text("Username Login *") },
                    placeholder = { Text("cth: rizky / bu_rahma") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password
                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Kata Sandi (Password) *") },
                    placeholder = { Text("Minimal 3 karakter") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Class or Subject
                if (selectedRole == UserRole.STUDENT) {
                    Text("Pilih Kelas Kamu:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("VII-A", "VII-B", "VIII-B").forEach { cls ->
                            FilterChip(
                                selected = classNameOrSubject == cls,
                                onClick = { classNameOrSubject = cls },
                                label = { Text(cls, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BilindiBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = classNameOrSubject,
                    onValueChange = { classNameOrSubject = it },
                    label = { Text(if (selectedRole == UserRole.TEACHER) "Mata Pelajaran yang Diampu" else "Kelas Siswa (cth: VII-A)") },
                    placeholder = { Text(if (selectedRole == UserRole.TEACHER) "cth: IPA / Matematika" else "cth: VII-A / VIII-B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // NISN / NIP
                OutlinedTextField(
                    value = nipOrNisn,
                    onValueChange = { nipOrNisn = it },
                    label = { Text(if (selectedRole == UserRole.TEACHER) "NIP / NUPTK (Opsional)" else "NISN Siswa (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        Toast.makeText(context, "Silakan isi nama lengkap Anda", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (regUsername.isBlank()) {
                        Toast.makeText(context, "Silakan tentukan username login", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (regPassword.isBlank()) {
                        Toast.makeText(context, "Silakan tentukan kata sandi Anda", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val registered = BilindiWallRepository.instance.registerUser(
                        name = fullName.trim(),
                        role = selectedRole,
                        username = regUsername.trim(),
                        password = regPassword.trim(),
                        className = if (selectedRole == UserRole.STUDENT) classNameOrSubject else null,
                        subject = if (selectedRole == UserRole.TEACHER) classNameOrSubject else null,
                        avatarUrl = avatarUri
                    )
                    Toast.makeText(context, "Registrasi berhasil! Selamat datang, ${registered.name}", Toast.LENGTH_SHORT).show()
                    onRegistered(registered)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LearningGreen)
            ) {
                Text("Daftar & Masuk")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

