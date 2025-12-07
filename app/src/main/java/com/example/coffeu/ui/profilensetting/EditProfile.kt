package com.example.coffeu.ui.profilensetting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    fullName: String,
    email: String,
    phoneNumber: String,
    dateOfBirth: String,
    onBackClicked: () -> Unit
) {
    var currentFullName by remember { mutableStateOf(fullName) }
    var currentEmail by remember { mutableStateOf(email) }
    var currentPhoneNumber by remember { mutableStateOf(phoneNumber) }
    var currentDateOfBirth by remember { mutableStateOf(dateOfBirth) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Data", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box {
                Image(
                    painter = painterResource(id = R.drawable.fanny),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                FloatingActionButton(
                    onClick = { /* TODO: Handle edit image */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Image", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            OutlinedTextField(
                value = currentFullName,
                onValueChange = { currentFullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentEmail,
                onValueChange = { currentEmail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentPhoneNumber,
                onValueChange = { currentPhoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentDateOfBirth,
                onValueChange = { currentDateOfBirth = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Save Changes */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun EditProfileScreenPreview() {
    CoffeUTheme {
        EditProfileScreen(
            fullName = "Lucas Nathan",
            email = "lucas@09gmail.com",
            phoneNumber = "308.555.0121",
            dateOfBirth = "November 24, 2000",
            onBackClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun EditProfileScreenDarkPreview() {
    CoffeUTheme(darkTheme = true) {
        EditProfileScreen(
            fullName = "Lucas Nathan",
            email = "lucas@09gmail.com",
            phoneNumber = "308.555.0121",
            dateOfBirth = "November 24, 2000",
            onBackClicked = {}
        )
    }
}
