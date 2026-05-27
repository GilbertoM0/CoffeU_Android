package com.example.coffeu.di

import android.content.Context
import android.content.SharedPreferences
import com.example.coffeu.BuildConfig
import com.example.coffeu.data.api.AuthService
import com.example.coffeu.data.remote.AuthApi
import com.example.coffeu.data.remote.AuthInterceptor
import com.example.coffeu.data.remote.RetrofitProvider
import com.example.coffeu.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(sessionManager: SessionManager): SharedPreferences {
        return sessionManager.sharedPreferences
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return RetrofitProvider.createOkHttpClient(authInterceptor = authInterceptor)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitProvider.createRetrofit(
            okHttpClient = okHttpClient,
            baseUrl = BuildConfig.API_BASE_URL
        )
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}
