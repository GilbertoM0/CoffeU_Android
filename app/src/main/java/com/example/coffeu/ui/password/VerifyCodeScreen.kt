package com.example.coffeu.ui.password

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeu.ui.theme.CoffeUTheme
import com.example.coffeu.ui.viewmodel.AuthUiState
import com.example.coffeu.ui.viewmodel.AuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    phoneNumber: String,
    onBackClicked: () -> Unit,
    onVerificationSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val firebaseAuth = remember { FirebaseAuth.getInstance() }

    var otpValue by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf(60) }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val verifyState by authViewModel.firebaseVerifyState.collectAsState()

    fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { signInTask ->
                if (!signInTask.isSuccessful) {
                    localError = "OTP inválido. Revisa el código e inténtalo de nuevo."
                    return@addOnCompleteListener
                }

                firebaseAuth.currentUser
                    ?.getIdToken(true)
                    ?.addOnCompleteListener { tokenTask ->
                        val idToken = tokenTask.result?.token
                        if (tokenTask.isSuccessful && !idToken.isNullOrBlank()) {
                            authViewModel.verificarFirebase(idToken)
                        } else {
                            localError = "No se pudo obtener el token de Firebase."
                        }
                    }
            }
    }

    val callbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                localError = "No se pudo enviar/verificar el OTP: ${e.localizedMessage ?: "sin detalle"}"
            }

            override fun onCodeSent(
                verificationIdValue: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = verificationIdValue
                resendToken = token
            }
        }
    }

    LaunchedEffect(phoneNumber, activity) {
        if (activity == null) {
            localError = "No se pudo iniciar Firebase Phone Auth en esta pantalla."
            return@LaunchedEffect
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        resendToken?.let { optionsBuilder.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    LaunchedEffect(key1 = verifyState) {
        if (verifyState is AuthUiState.Success) {
            authViewModel.resetFirebaseVerifyUiState()
            onVerificationSuccess()
        }
    }

    LaunchedEffect(key1 = seconds) {
        while (seconds > 0) {
            delay(1000L)
            seconds--
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Verifica tu código",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ingresa el código de 6 dígitos que enviamos a $phoneNumber",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))

            OtpTextField(otpText = otpValue, onOtpTextChange = { value, _ -> otpValue = value })

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Reenviar código en 00:${String.format("%02d", seconds)}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            val backendError = (verifyState as? AuthUiState.Error)?.message
            val resolvedError = localError ?: backendError
            if (!resolvedError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = resolvedError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val code = otpValue.trim()
                    val currentVerificationId = verificationId
                    if (code.length != 6 || currentVerificationId.isNullOrBlank()) {
                        localError = "Ingresa un OTP válido de 6 dígitos."
                    } else {
                        localError = null
                        val credential = PhoneAuthProvider.getCredential(currentVerificationId, code)
                        signInWithCredential(credential)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = verifyState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (verifyState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Verificar código", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) {
                onOtpTextChange.invoke(it, it.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) {
                    val char = otpText.getOrNull(it)
                    OtpChar(char = char, hasFocus = it == otpText.length)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun OtpChar(char: Char?, hasFocus: Boolean) {
    val border = if (hasFocus) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color.LightGray)
    Box(
        modifier = Modifier
            .size(50.dp)
            .border(border, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        char?.let {
            Text(text = it.toString(), fontSize = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    CoffeUTheme {
        VerifyCodeScreen(phoneNumber = "+521234567890", onBackClicked = {}, onVerificationSuccess = {})
    }
}
