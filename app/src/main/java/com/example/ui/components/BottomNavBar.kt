package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BilindiBlue

enum class NavTab(
    val id: String,
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    HOME("home", "Beranda", Icons.Default.Home, Icons.Outlined.Home),
    REELS("reels", "Reels", Icons.Default.PlayCircle, Icons.Outlined.PlayCircle),
    ASSIGNMENTS("assignments", "Tugas & Nilai", Icons.Default.Assignment, Icons.Outlined.Assignment),
    CLASSES("classes", "Kelas", Icons.Default.School, Icons.Outlined.School),
    PROFILE("profile", "Profil", Icons.Default.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BilindiBlue,
                    selectedTextColor = BilindiBlue,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("tab_${tab.id}")
            )
        }
    }
}
