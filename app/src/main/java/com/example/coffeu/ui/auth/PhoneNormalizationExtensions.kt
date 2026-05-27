package com.example.coffeu.ui.auth

private val allowedPhoneCharsRegex = Regex("[^+\\d]")

fun String.normalizarTelefonoParaBackend(): String {
    val raw = trim().replace(allowedPhoneCharsRegex, "")
    if (raw.isBlank()) return ""

    fun String.digitsOnly(): String = filter(Char::isDigit)

    if (raw.startsWith("+52")) {
        val remainder = raw
            .removePrefix("+52")
            .replace("+52", "")
            .replace("+", "")
            .digitsOnly()
        return "+52$remainder"
    }

    if (raw.startsWith("52")) {
        val remainder = raw
            .removePrefix("52")
            .replace("+52", "")
            .replace("+", "")
            .digitsOnly()
        return "+52$remainder"
    }

    return raw.replace("+", "").digitsOnly()
}

fun String.construirTelefonoConPicker(countryCode: String): String {
    val phoneInput = normalizarTelefonoParaBackend()
    if (phoneInput.startsWith("+")) return phoneInput

    val normalizedCountryCode = countryCode.trim().ifBlank { "+52" }
    return "$normalizedCountryCode$phoneInput".normalizarTelefonoParaBackend()
}
