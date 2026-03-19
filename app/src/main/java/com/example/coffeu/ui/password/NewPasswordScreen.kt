package com.example.coffeu.ui.password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coffeu.R
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.NewPasswordUiState
import com.example.coffeu.ui.viewmodel.NewPasswordViewModel

@Composable
fun NewPasswordScreen(
    onBackClicked: () -> Unit,
    onCreatePasswordClicked: () -> Unit,
    viewModel: NewPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewPasswordScreenContent(
        uiState = uiState,
        identifier = viewModel.identifier,
        otp = viewModel.otp,
        newPassword = viewModel.newPassword,
        confirmPassword = viewModel.confirmPassword,
        onIdentifierChange = { viewModel.identifier = it },
        onOtpChange = { viewModel.otp = it },
        onNewPasswordChange = { viewModel.newPassword = it },
        onConfirmPasswordChange = { viewModel.confirmPassword = it },
        onBackClicked = onBackClicked,
        onRequestOtp = { viewModel.requestOtp() },
        onCreatePassword = { viewModel.createNewPassword() },
        onResetState = { viewModel.resetState() },
        onCreatePasswordClicked = onCreatePasswordClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreenContent(
    uiState: NewPasswordUiState,
    identifier: String,
    otp: String,
    newPassword: String,
    confirmPassword: String,
    onIdentifierChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onBackClicked: () -> Unit,
    onRequestOtp: () -> Unit,
    onCreatePassword: () -> Unit,
    onResetState: () -> Unit,
    onCreatePasswordClicked: () -> Unit
) {
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val msgOtpSent = stringResource(id = R.string.msg_otp_sent_success)
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is NewPasswordUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                onResetState()
            }
            NewPasswordUiState.OtpSent -> {
                snackbarHostState.showSnackbar(msgOtpSent)
                onResetState()
            }
            NewPasswordUiState.Success -> {
                onCreatePasswordClicked()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.reset_password_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.reset_password_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = identifier,
                onValueChange = onIdentifierChange,
                label = { Text(stringResource(id = R.string.label_identifier)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRequestOtp,
                enabled = uiState != NewPasswordUiState.Loading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(id = R.string.button_send_otp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = onOtpChange,
                label = { Text(stringResource(id = R.string.label_otp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(id = R.string.label_new_password), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.Gray)
            OutlinedTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "toggle password visibility")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(id = R.string.label_confirm_password), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.Gray)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "toggle password visibility")
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCreatePassword,
                enabled = uiState != NewPasswordUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState == NewPasswordUiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(stringResource(id = R.string.button_save_new_password), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
