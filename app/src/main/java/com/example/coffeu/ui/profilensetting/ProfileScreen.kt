package com.example.coffeu.ui.profilensetting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Header
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.fanny),
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
                text = "General",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        item { ProfileMenuItem(icon = Icons.Default.Person, text = "Edit Profile", onClick = onNavigateToEditProfile) }
        item { ProfileMenuItem(icon = Icons.Default.Lock, text = "Change Password", onClick = onNavigateToChangePassword) }
        item { ProfileMenuItem(icon = Icons.Default.Notifications, text = "Notifications", onClick = onNavigateToNotifications) }
        item { ProfileMenuItem(icon = Icons.Default.Settings, text = "Security") {} }
        item { ProfileMenuItem(icon = Icons.Default.Info, text = "Language") {} }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to $text",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            onNavigateToEditProfile = {},
            onNavigateToNotifications = {},
            onNavigateToChangePassword = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ProfileScreenDarkPreview() {
    CoffeUTheme(darkTheme = true) {
        ProfileScreen(
            userName = "Lucas Nathan",
            userEmail = "wilson@09gail.com",
            onNavigateToEditProfile = {},
            onNavigateToNotifications = {},
            onNavigateToChangePassword = {}
        )
    }
}
