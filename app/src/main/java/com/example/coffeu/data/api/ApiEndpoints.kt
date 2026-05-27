package com.example.coffeu.data.api

object ApiEndpoints {
	private const val ACCOUNTS_BASE = "accounts/"

	const val REGISTRO = "${ACCOUNTS_BASE}registro/"
	const val LOGIN = "${ACCOUNTS_BASE}login/"
	const val FIREBASE_VERIFY = "${ACCOUNTS_BASE}firebase-verify/"
	const val FORGOT = "${ACCOUNTS_BASE}forgot/"
	const val RESET = "${ACCOUNTS_BASE}reset/"
	const val ACTIVAR = "${ACCOUNTS_BASE}activar/"
	const val REFRESH = "${ACCOUNTS_BASE}refresh/"
	const val UPDATE_PROFILE = "${ACCOUNTS_BASE}update-profile/"

	private val publicPathSuffixes = setOf(
		REGISTRO,
		LOGIN,
		FIREBASE_VERIFY,
		FORGOT,
		RESET,
		ACTIVAR,
		REFRESH
	).map { endpoint -> "/$endpoint" }

	fun isPublicPath(encodedPath: String): Boolean {
		return publicPathSuffixes.any { publicPath ->
			encodedPath == publicPath || encodedPath.endsWith(publicPath)
		}
	}
}

