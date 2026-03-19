package com.example.coffeu.ui.profilensetting

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onBackClicked: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToEditLanguage: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    }
    val imageUri = sharedPreferences.getString("image_uri", null)?.let { Uri.parse(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.profile_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = imageUri ?: R.drawable.fanny,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // General Section
            item {
                Text(
                    text = stringResource(id = R.string.profile_section_general),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item { ProfileMenuItem(icon = Icons.Default.Person, text = stringResource(id = R.string.profile_menu_edit_profile), onClick = onNavigateToEditProfile) }
            item { ProfileMenuItem(icon = Icons.Default.Lock, text = stringResource(id = R.string.profile_menu_change_password), onClick = onNavigateToChangePassword) }
            item { ProfileMenuItem(icon = Icons.Default.Notifications, text = stringResource(id = R.string.profile_menu_notifications), onClick = onNavigateToNotifications) }
            item { ProfileMenuItem(icon = Icons.Default.Settings, text = stringResource(id = R.string.profile_menu_security)) {} }
            item { ProfileMenuItem(icon = Icons.Default.Info, text = stringResource(id = R.string.profile_menu_language), onClick = onNavigateToEditLanguage) }

            // Logout Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Logout, text = stringResource(id = R.string.profile_menu_logout), onClick = onLogout)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    val logoutText = stringResource(id = R.string.profile_menu_logout)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (text == logoutText) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = if (text == logoutText) Color.Red else MaterialTheme.colorScheme.onSurface
            )
            if (text != logoutText) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to $text",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ProfileScreenPreview() {
    CoffeUTheme {
        ProfileScreen(
            userName = "Lucas Nathan",
            userEmail = "wilson@09gail.com",
            onBackClicked = {},
            onNavigateToEditProfile = {},
            onNavigateToNotifications = {},
            onNavigateToChangePassword = {},
            onLogout = {}
        )
    }
}
