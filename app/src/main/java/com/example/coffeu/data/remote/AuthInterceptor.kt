package com.example.coffeu.data.remote

import com.example.coffeu.data.api.ApiEndpoints
import com.example.coffeu.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val encodedPath = originalRequest.url.encodedPath

        if (ApiEndpoints.isPublicPath(encodedPath)) {
            return chain.proceed(originalRequest)
        }

        if (!originalRequest.header(AUTHORIZATION_HEADER).isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val accessToken = sessionManager.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX $accessToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer"
    }
}
