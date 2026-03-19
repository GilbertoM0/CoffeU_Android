package com.example.coffeu.ui.profilensetting

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthViewModel
import java.util.Calendar

@Composable
fun EditProfileScreen(
    fullName: String,
    email: String,
    phoneNumber: String,
    dateOfBirth: String,
    onBackClicked: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    EditProfileContent(
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        dateOfBirth = dateOfBirth,
        onBackClicked = onBackClicked,
        isLoading = authViewModel.isLoading,
        updateSuccess = authViewModel.updateProfileSuccess,
        errorMessage = authViewModel.errorMessage,
        onUpdateProfile = { name, mail, phone, dob ->
            authViewModel.attemptUpdateProfile(
                nombre_usuario = name,
                email = mail,
                telefono_celular = phone,
                fecha_nacimiento = dob
            )
        },
        onResetUpdateState = { authViewModel.resetUpdateProfileState() },
        onUpdateErrorMessage = { authViewModel.updateErrorMessage(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    fullName: String,
    email: String,
    phoneNumber: String,
    dateOfBirth: String,
    onBackClicked: () -> Unit,
    isLoading: Boolean,
    updateSuccess: Boolean,
    errorMessage: String?,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onResetUpdateState: () -> Unit,
    onUpdateErrorMessage: (String?) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    }

    // Parsing initial date (YYYY-MM-DD)
    val initialParts = dateOfBirth.split("-")
    val initialYear = initialParts.getOrNull(0) ?: ""
    val initialMonth = initialParts.getOrNull(1) ?: ""
    val initialDay = initialParts.getOrNull(2) ?: ""

    var currentFullName by remember { mutableStateOf(fullName) }
    var currentEmail by remember { mutableStateOf(email) }
    var currentPhoneNumber by remember { mutableStateOf(phoneNumber) }
    
    // States for the 3-dropdown date picker
    var selectedDay by remember { mutableStateOf(initialDay) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedYear by remember { mutableStateOf(initialYear) }

    var imageUri by remember { mutableStateOf(sharedPreferences.getString("image_uri", null)?.let { Uri.parse(it) }) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    val snackbarHostState = remember { SnackbarHostState() }

    // Helper data for dropdowns
    val months = listOf(
        "01" to "Enero", "02" to "Febrero", "03" to "Marzo", "04" to "Abril",
        "05" to "Mayo", "06" to "Junio", "07" to "Julio", "08" to "Agosto",
        "09" to "Septiembre", "10" to "Octubre", "11" to "Noviembre", "12" to "Diciembre"
    )
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear downTo 1900).map { it.toString() }

    // Logic for days validation
    val daysInMonth = remember(selectedMonth, selectedYear) {
        val monthInt = selectedMonth.toIntOrNull() ?: 1
        val yearInt = selectedYear.toIntOrNull() ?: 2000
        val calendar = Calendar.getInstance()
        calendar.set(yearInt, monthInt - 1, 1)
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Adjust selected day if it exceeds the max days of the newly selected month/year
    LaunchedEffect(daysInMonth) {
        val dayInt = selectedDay.toIntOrNull() ?: 1
        if (dayInt > daysInMonth) {
            selectedDay = daysInMonth.toString().padStart(2, '0')
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar("Perfil actualizado correctamente")
            onResetUpdateState()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onUpdateErrorMessage(null)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box {
                AsyncImage(
                    model = imageUri ?: R.drawable.fanny,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                FloatingActionButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
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

            OutlinedTextField(
                value = currentFullName,
                onValueChange = { currentFullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentEmail,
                onValueChange = { currentEmail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentPhoneNumber,
                onValueChange = { currentPhoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Date of Birth Dropdowns
            Text(
                text = "Fecha de nacimiento",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Day Dropdown
                var dayExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Día") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        (1..daysInMonth).forEach { day ->
                            val dayStr = day.toString().padStart(2, '0')
                            DropdownMenuItem(
                                text = { Text(dayStr) },
                                onClick = {
                                    selectedDay = dayStr
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                // Month Dropdown
                var monthExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = monthExpanded,
                    onExpandedChange = { monthExpanded = it },
                    modifier = Modifier.weight(1.5f)
                ) {
                    val currentMonthName = months.find { it.first == selectedMonth }?.second ?: "Mes"
                    OutlinedTextField(
                        value = currentMonthName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mes") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = monthExpanded,
                        onDismissRequest = { monthExpanded = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(month.second) },
                                onClick = {
                                    selectedMonth = month.first
                                    monthExpanded = false
                                }
                            )
                        }
                    }
                }

                // Year Dropdown
                var yearExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = yearExpanded,
                    onExpandedChange = { yearExpanded = it },
                    modifier = Modifier.weight(1.2f)
                ) {
                    OutlinedTextField(
                        value = selectedYear,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Año") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = yearExpanded,
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year) },
                                onClick = {
                                    selectedYear = year
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    imageUri?.let {
                        sharedPreferences.edit().putString("image_uri", it.toString()).apply()
                    }
                    
                    // Construct final date string: YYYY-MM-DD
                    val finalDate = "$selectedYear-$selectedMonth-$selectedDay"
                    
                    onUpdateProfile(
                        currentFullName,
                        currentEmail,
                        currentPhoneNumber,
                        finalDate
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun EditProfileScreenPreview() {
    CoffeUTheme {
        EditProfileContent(
            fullName = "Lucas Nathan",
            email = "lucas@09gmail.com",
            phoneNumber = "308.555.0121",
            dateOfBirth = "2000-11-24",
            onBackClicked = {},
            isLoading = false,
            updateSuccess = false,
            errorMessage = null,
            onUpdateProfile = { _, _, _, _ -> },
            onResetUpdateState = {},
            onUpdateErrorMessage = {}
        )
    }
}
