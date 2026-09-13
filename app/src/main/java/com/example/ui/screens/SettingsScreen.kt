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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BilindiWallRepository
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.components.BilindiLogoIcon
import com.example.ui.components.ChangeAvatarDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: BilindiWallRepository,
    currentUser: User,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(currentUser.name) }
    var selectedAvatar by remember { mutableStateOf(currentUser.avatarUrl) }
    var selectedCover by remember { mutableStateOf(currentUser.coverUrl) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoModerationAlerts by remember { mutableStateOf(true) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    // App logo management states (Teacher / Principal)
    val currentAppLogo by repository.appLogoUrl.collectAsState()
    var showLogoUrlDialog by remember { mutableStateOf(false) }
    var inputLogoUrl by remember { mutableStateOf("") }

    // Photo picker for teacher / admin to change application logo
    val appLogoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            repository.updateAppLogo(uri.toString())
            Toast.makeText(context, "Logo aplikasi berhasil diubah dari galeri!", Toast.LENGTH_SHORT).show()
        }
    }

    // Native Android Photo Picker launcher for user avatar
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAvatar = uri.toString()
            Toast.makeText(context, "Foto berhasil dipilih dari galeri perangkat!", Toast.LENGTH_SHORT).show()
        }
    }

    val presetAvatars = if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
        listOf(
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?auto=format&fit=crop&q=80&w=300"
        )
    } else {
        listOf(
            "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300",
            "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&q=80&w=300"
        )
    }

    val presetCovers = listOf(
        "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?auto=format&fit=crop&q=80&w=1000",
        "https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&q=80&w=1000",
        "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&q=80&w=1000"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen")
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Pengaturan Akun & Sekolah", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo Management Card (Khusus Menu Guru & Kepala Sekolah)
            if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.testTag("app_logo_management_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BilindiBlueLight,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = BilindiBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Logo Aplikasi & Identitas Sekolah",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Menu Guru: Unggah & ganti logo resmi aplikasi sekolah",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        // Current Logo Active Preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, BilindiBlue),
                                shadowElevation = 2.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BilindiLogoIcon(size = 54.dp)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (currentAppLogo != null) LearningGreen.copy(alpha = 0.15f) else BilindiBlueLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (currentAppLogo != null) "✓ Logo Kustom Terpasang" else "• Logo Bawaan BilindiWall",
                                            color = if (currentAppLogo != null) LearningGreen else BilindiBlue,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentAppLogo != null)
                                        "Logo ini sekarang tampil di bar atas, beranda, dan halaman login."
                                    else
                                        "Gunakan tombol di bawah untuk mengganti logo dengan lambang sekolah Anda.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                                if (currentAppLogo != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = {
                                            repository.updateAppLogo(null)
                                            Toast.makeText(context, "Logo aplikasi dikembalikan ke default!", Toast.LENGTH_SHORT).show()
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("↺ Kembalikan ke Logo Default", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Upload actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    appLogoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("teacher_upload_app_logo_btn")
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload dari Galeri", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showLogoUrlDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("teacher_url_app_logo_btn")
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp), tint = BilindiBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Input URL Logo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BilindiBlue)
                            }
                        }

                        // Quick Presets from repository
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Atau pilih cepat contoh lambang sekolah/instansi:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(repository.presetSchoolLogos) { logoUrl ->
                                    val isSelected = currentAppLogo == logoUrl
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) BilindiBlue else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                repository.updateAppLogo(logoUrl)
                                                Toast.makeText(context, "Logo aplikasi berhasil diganti!", Toast.LENGTH_SHORT).show()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = logoUrl,
                                            contentDescription = "Pilihan Lambang Sekolah",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(BilindiBlue.copy(alpha = 0.35f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
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
            }

            // Profile Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Informasi Profil & Identitas (${currentUser.role.label})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Avatar Preview & Upload Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(3.dp, BilindiBlue, CircleShape)
                                .clickable { showAvatarDialog = true }
                        ) {
                            AsyncImage(
                                model = selectedAvatar,
                                contentDescription = "Avatar Pengguna",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Foto Profil ${currentUser.role.label}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("settings_upload_avatar_button")
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload dari Galeri", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Avatar Selector
                    Text(
                        text = if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL)
                            "Pilihan Avatar Guru / Tenaga Pendidik:"
                        else
                            "Pilihan Avatar Siswa:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(presetAvatars) { url ->
                            val isSelected = selectedAvatar == url
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(if (isSelected) 3.dp else 1.dp, if (isSelected) LearningGreen else Color.LightGray, CircleShape)
                                    .clickable { selectedAvatar = url }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Cover selector (for teachers and principal)
                    if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
                        Text("Pilih Foto Sampul / Banner Instansi:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(presetCovers) { url ->
                                val isSelected = selectedCover == url
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BilindiBlue) else null,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(60.dp)
                                        .clickable { selectedCover = url }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Preferences Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Preferensi Tampilan & Notifikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Dark Mode Toggle
                    val isDarkMode by repository.isDarkMode.collectAsState()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mode Gelap (Dark Mode)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Alternatif latar belakang gelap yang nyaman di mata", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { repository.setDarkMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = BilindiBlue)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notifikasi Tugas & Materi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Dapatkan kabar saat guru menerbitkan modul baru", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BilindiBlue)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Peringatan Moderasi Otomatis", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Informasi status peninjauan postingan siswa", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoModerationAlerts,
                            onCheckedChange = { autoModerationAlerts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BilindiBlue)
                        )
                    }
                }
            }

            // Save changes button
            Button(
                onClick = {
                    repository.updateUserProfile(
                        name = name,
                        avatarUrl = selectedAvatar,
                        coverUrl = selectedCover,
                        bio = currentUser.bio
                    )
                    Toast.makeText(context, "Pengaturan profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_button")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            // Logout / Switch Role
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar / Ganti Akun Pengguna", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "BilindiWall • TIM IT SMP Negeri sinombayuga",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 12.dp)
            )
        }
    }

    if (showAvatarDialog) {
        ChangeAvatarDialog(
            user = currentUser.copy(avatarUrl = selectedAvatar),
            onAvatarUpdated = { newUrl ->
                selectedAvatar = newUrl
            },
            onDismiss = { showAvatarDialog = false }
        )
    }

    if (showLogoUrlDialog) {
        AlertDialog(
            onDismissRequest = { showLogoUrlDialog = false },
            title = {
                Text(
                    text = "Tautan URL Logo Aplikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Masukkan alamat URL gambar logo sekolah (PNG, JPG, SVG, WebP):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputLogoUrl,
                        onValueChange = { inputLogoUrl = it },
                        placeholder = { Text("https://contoh.sch.id/logo.png") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputLogoUrl.isNotBlank()) {
                            repository.updateAppLogo(inputLogoUrl.trim())
                            Toast.makeText(context, "Logo aplikasi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                            showLogoUrlDialog = false
                            inputLogoUrl = ""
                        } else {
                            Toast.makeText(context, "Silakan masukkan URL gambar logo yang valid", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BilindiBlue)
                ) {
                    Text("Terapkan Logo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoUrlDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
