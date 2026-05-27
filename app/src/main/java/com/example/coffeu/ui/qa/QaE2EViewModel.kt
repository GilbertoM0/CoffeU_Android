package com.example.coffeu.ui.qa

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.model.FirebaseVerifyRequest
import com.example.coffeu.data.model.RegistroRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class QaE2EViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    var statusText by mutableStateOf("Esperando acción...")
        private set
    var isLoading by mutableStateOf(false)
        private set

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    // ── PASO 1: Registrar en el backend ─────────────────────────────────────
    fun registrar(
        nombre: String,
        telefono: String,
        email: String,
        password: String,
        password2: String
    ) {
        if (nombre.isBlank() || telefono.isBlank() || email.isBlank() ||
            password.isBlank() || password2.isBlank()
        ) {
            statusText = "❌ Completa todos los campos antes de registrar."
            return
        }

        val request = RegistroRequest(
            nombreUsuario = nombre.trim(),
            telefonoCelular = telefono.trim(),
            email = email.trim(),
            password = password,
            password2 = password2
        )
        Log.d("REGISTRO_REQUEST", "POST /accounts/registro/ → $request")

        viewModelScope.launch {
            isLoading = true
            statusText = "⏳ [Paso 1] Registrando usuario en backend..."
            try {
                val response = authService.registro(request)
                Log.d("REGISTRO_RESPONSE", "✅ 201 → ${response.mensaje}")
                statusText = "✅ [Paso 1] Registro OK\n${response.mensaje ?: "Usuario creado."}"
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string() ?: "sin cuerpo"
                Log.e("REGISTRO_RESPONSE", "❌ HTTP ${e.code()} → $body")
                statusText = "❌ [Paso 1] Error HTTP ${e.code()}: $body"
            } catch (e: IOException) {
                Log.e("REGISTRO_RESPONSE", "❌ Red: ${e.message}")
                statusText = "❌ [Paso 1] Error de red: ${e.message}"
            } catch (e: Exception) {
                Log.e("REGISTRO_RESPONSE", "❌ Inesperado: ${e.message}")
                statusText = "❌ [Paso 1] Error inesperado: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // ── PASO 2: Enviar OTP con Firebase Phone Auth ───────────────────────────
    fun enviarOtpFirebase(activity: Activity, telefono: String) {
        if (telefono.isBlank()) {
            statusText = "❌ Ingresa el teléfono antes de enviar OTP."
            return
        }

        Log.d("OTP_CODE_SENT", "Iniciando verifyPhoneNumber para: $telefono")
        isLoading = true
        statusText = "⏳ [Paso 2] Enviando OTP a $telefono..."

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("OTP_CODE_SENT", "✅ onVerificationCompleted (auto-detect)")
                statusText = "✅ [Paso 2] OTP detectado automáticamente.\n⏳ Verificando..."
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("OTP_CODE_SENT", "❌ onVerificationFailed: ${e.message}")
                statusText = "❌ [Paso 2] Falló el envío de OTP:\n${e.message}"
                isLoading = false
            }

            override fun onCodeSent(
                id: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = id
                resendToken = token
                Log.d("OTP_CODE_SENT", "✅ Código enviado. verificationId=${id.take(10)}...")
                statusText = "✅ [Paso 2] OTP enviado a $telefono.\nIngresa el código de 6 dígitos y presiona Verificar."
                isLoading = false
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(telefono)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
        resendToken?.let { optionsBuilder.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    // ── PASO 3: Verificar OTP manualmente y enviar idToken al backend ────────
    fun verificarOtpYEnviarIdToken(otpCode: String) {
        val currentVerificationId = verificationId
        when {
            currentVerificationId.isNullOrBlank() -> {
                statusText = "❌ [Paso 3] Primero envía el OTP (Paso 2) para obtener verificationId."
                return
            }
            otpCode.trim().length != 6 -> {
                statusText = "❌ [Paso 3] El OTP debe tener exactamente 6 dígitos."
                return
            }
        }
        Log.d("FIREBASE_SIGNIN", "Creando credential con verificationId + otp")
        isLoading = true
        statusText = "⏳ [Paso 3] Verificando OTP en Firebase..."
        val credential = PhoneAuthProvider.getCredential(currentVerificationId, otpCode.trim())
        signInWithCredential(credential)
    }

    // ── Interno: sign in + obtener idToken + llamar backend ─────────────────
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    Log.e("FIREBASE_SIGNIN_FAIL", "user es null tras signInWithCredential")
                    statusText = "❌ Firebase: usuario nulo tras sign in."
                    isLoading = false
                    return@addOnSuccessListener
                }
                Log.d("FIREBASE_SIGNIN_OK", "uid=${user.uid} | phone=${user.phoneNumber}")
                statusText = "⏳ [Paso 3] Firebase OK (uid=${user.uid.take(8)}...).\nObteniendo idToken..."

                user.getIdToken(true).addOnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful || tokenTask.result?.token.isNullOrBlank()) {
                        val err = tokenTask.exception?.message ?: "sin detalle"
                        Log.e("ID_TOKEN_OBTENIDO", "❌ getIdToken falló: $err")
                        statusText = "❌ No se pudo obtener idToken: $err"
                        isLoading = false
                        return@addOnCompleteListener
                    }
                    val idToken = tokenTask.result!!.token!!
                    Log.d("ID_TOKEN_OBTENIDO", "✅ idToken(30): ${idToken.take(30)}...")
                    statusText = "⏳ [Paso 3] idToken obtenido.\nEnviando a /accounts/firebase-verify/..."
                    enviarIdTokenAlBackend(idToken)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_SIGNIN_FAIL", "❌ signInWithCredential falló: ${e.message}")
                statusText = "❌ [Paso 3] OTP inválido o expirado:\n${e.message}"
                isLoading = false
            }
    }

    private fun enviarIdTokenAlBackend(idToken: String) {
        val request = FirebaseVerifyRequest(idToken = idToken)
        Log.d("FIREBASE_VERIFY_BACKEND_REQUEST",
            "POST /accounts/firebase-verify/ | id_token(30)=${idToken.take(30)}...")

        viewModelScope.launch {
            try {
                val response = authService.firebaseVerify(request)
                Log.d("FIREBASE_VERIFY_BACKEND_RESPONSE", buildString {
                    appendLine("✅ 200 OK")
                    appendLine("mensaje       : ${response.mensaje}")
                    appendLine("access_token  : ${response.accessToken.take(30)}...")
                    appendLine("refresh_token : ${response.refreshToken.take(30)}...")
                    appendLine("user.id       : ${response.user.id}")
                    appendLine("user.nombre   : ${response.user.nombreUsuario}")
                    appendLine("user.email    : ${response.user.email}")
                    appendLine("user.telefono : ${response.user.telefonoCelular}")
                })
                statusText = buildString {
                    appendLine("🎉 FLUJO E2E COMPLETADO")
                    appendLine("━━━━━━━━━━━━━━━━━━━━━━━")
                    appendLine("Usuario : ${response.user.nombreUsuario}")
                    appendLine("Email   : ${response.user.email}")
                    appendLine("Teléfono: ${response.user.telefonoCelular}")
                    appendLine("access  : ${response.accessToken.take(25)}...")
                    appendLine("refresh : ${response.refreshToken.take(25)}...")
                    response.mensaje?.let { appendLine("Backend : $it") }
                }
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string() ?: "sin cuerpo"
                Log.e("FIREBASE_VERIFY_BACKEND_RESPONSE", "❌ HTTP ${e.code()} → $body")
                statusText = "❌ [Paso 3] Backend HTTP ${e.code()}:\n$body"
            } catch (e: IOException) {
                Log.e("FIREBASE_VERIFY_BACKEND_RESPONSE", "❌ Red: ${e.message}")
                statusText = "❌ [Paso 3] Error de red en firebase-verify:\n${e.message}"
            } catch (e: Exception) {
                Log.e("FIREBASE_VERIFY_BACKEND_RESPONSE", "❌ Inesperado: ${e.message}")
                statusText = "❌ [Paso 3] Error inesperado:\n${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
