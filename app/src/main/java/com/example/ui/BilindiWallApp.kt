package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BilindiWallRepository
import com.example.model.User
import com.example.ui.components.*
import com.example.ui.screens.*

@Composable
fun BilindiWallApp(
    repository: BilindiWallRepository = remember { BilindiWallRepository.instance }
) {
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState()
    val usersMap by repository.users.collectAsState()
    val announcements by repository.announcements.collectAsState()

    var selectedTab by remember { mutableStateOf(NavTab.HOME) }
    var searchQuery by remember { mutableStateOf("") }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var isMessengerOpen by remember { mutableStateOf(false) }
    var showUserSwitchDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // If user is not logged in, show LoginScreen
    val user = currentUser
    if (user == null) {
        LoginScreen(
            users = usersMap,
            onLogin = { selected ->
                repository.setCurrentUser(selected)
                selectedTab = NavTab.HOME
            }
        )
        return
    }

    if (isMessengerOpen) {
        MessengerScreen(
            repository = repository,
            currentUser = user,
            onBack = { isMessengerOpen = false }
        )
        return
    }

    if (isSettingsOpen) {
        SettingsScreen(
            repository = repository,
            currentUser = user,
            onLogout = {
                isSettingsOpen = false
                repository.setCurrentUser(null)
            },
            onBack = { isSettingsOpen = false }
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bilindi_wall_app"),
        topBar = {
            TopNavBar(
                currentUser = user,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onNotificationClick = { showNotificationsDialog = true },
                onMessageClick = { isMessengerOpen = true },
                onProfileClick = { selectedTab = NavTab.PROFILE },
                onSwitchUserClick = { showUserSwitchDialog = true }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                NavTab.HOME -> {
                    FeedScreen(
                        repository = repository,
                        currentUser = user,
                        searchQuery = searchQuery,
                        onHashtagFilter = { tag ->
                            searchQuery = tag
                        }
                    )
                }
                NavTab.REELS -> {
                    ReelsScreen(
                        repository = repository
                    )
                }
                NavTab.ASSIGNMENTS -> {
                    TeacherDashboardScreen(
                        repository = repository,
                        currentUser = user,
                        onNavigateToFeedWithHashtag = { tag ->
                            searchQuery = tag
                            selectedTab = NavTab.HOME
                        }
                    )
                }
                NavTab.CLASSES -> {
                    ClassesScreen(
                        repository = repository,
                        currentUser = user
                    )
                }
                NavTab.PROFILE -> {
                    ProfileScreen(
                        repository = repository,
                        currentUser = user,
                        onOpenSettings = { isSettingsOpen = true }
                    )
                }
            }
        }
    }

    // User Switch Dialog
    if (showUserSwitchDialog) {
        UserSwitchDialog(
            users = usersMap.values.toList(),
            currentUser = user,
            onSelectUser = { newUser ->
                repository.setCurrentUser(newUser)
                Toast.makeText(context, "Beralih ke akun: ${newUser.name} (${newUser.role.label})", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showUserSwitchDialog = false }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        Dialog(onDismissRequest = { showNotificationsDialog = false }) {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notifikasi & Pengumuman",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(10.dp))

                    announcements.forEach { ann ->
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = ann.title,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = ann.content,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ann.date,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showNotificationsDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}
