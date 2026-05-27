package com.example.coffeu.data.remote

import android.content.Context
import com.example.coffeu.BuildConfig
import com.example.coffeu.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    fun createSessionManager(context: Context): SessionManager {
        return SessionManager(context.applicationContext)
    }

    fun createOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return createOkHttpClient(AuthInterceptor(sessionManager))
    }

    fun createOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { message ->
                android.util.Log.d("OkHttp", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    fun createRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String = BuildConfig.API_BASE_URL
    ): Retrofit {
        require(baseUrl.endsWith('/')) {
            "La base URL debe terminar con '/': $baseUrl"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createAuthApi(
        context: Context,
        baseUrl: String = BuildConfig.API_BASE_URL
    ): AuthApi {
        val sessionManager = createSessionManager(context)
        val okHttpClient = createOkHttpClient(sessionManager)
        return createRetrofit(okHttpClient, baseUrl).create(AuthApi::class.java)
    }

    private const val TIMEOUT_SECONDS = 30L
}
