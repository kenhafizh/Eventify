package com.example.technical_test_indomarco.di

import android.content.Context
import com.example.technical_test_indomarco.BuildConfig
import com.example.technical_test_indomarco.data.local.AppDatabase
import com.example.technical_test_indomarco.data.local.SecureTokenManager
import com.example.technical_test_indomarco.data.remote.api.ApiConfig
import com.example.technical_test_indomarco.data.remote.api.ApiService
import com.example.technical_test_indomarco.data.repository.EventRepositoryImpl
import com.example.technical_test_indomarco.data.repository.LoginRepositoryImpl
import com.example.technical_test_indomarco.domain.repository.EventRepository
import com.example.technical_test_indomarco.domain.repository.LoginRepository
import com.example.technical_test_indomarco.domain.usecase.GetAllEventsUseCase
import com.example.technical_test_indomarco.domain.usecase.GetEventByIdUseCase
import com.example.technical_test_indomarco.domain.usecase.GetEventsPagedUseCase
import com.example.technical_test_indomarco.domain.usecase.LoginUseCase
import com.example.technical_test_indomarco.domain.usecase.SaveEventUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val primaryApiService: ApiService by lazy {
        createRetrofit(ApiConfig.PRIMARY_BASE_URL).create(ApiService::class.java)
    }

    private val fallbackApiService: ApiService by lazy {
        createRetrofit(ApiConfig.FALLBACK_BASE_URL).create(ApiService::class.java)
    }

    fun provideApiService(): ApiService = primaryApiService

    fun provideFallbackApiService(): ApiService = fallbackApiService
}

object AuthModule {
    fun provideSecureTokenManager(context: Context): SecureTokenManager =
        SecureTokenManager.getInstance(context)

    fun provideLoginRepository(context: Context): LoginRepository =
        LoginRepositoryImpl(
            primaryApiService = NetworkModule.provideApiService(),
            fallbackApiService = NetworkModule.provideFallbackApiService(),
            tokenManager = provideSecureTokenManager(context)
        )

    fun provideLoginUseCase(context: Context): LoginUseCase =
        LoginUseCase(provideLoginRepository(context))
}

object DatabaseModule {

    fun provideDatabase(context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    fun provideEventRepository(context: Context): EventRepository =
        EventRepositoryImpl(provideDatabase(context).eventDao())

    fun provideSaveEventUseCase(context: Context): SaveEventUseCase =
        SaveEventUseCase(provideEventRepository(context))

    fun provideGetAllEventsUseCase(context: Context): GetAllEventsUseCase =
        GetAllEventsUseCase(provideEventRepository(context))

    fun provideGetEventsPagedUseCase(context: Context): GetEventsPagedUseCase =
        GetEventsPagedUseCase(provideEventRepository(context))

    fun provideGetEventByIdUseCase(context: Context): GetEventByIdUseCase =
        GetEventByIdUseCase(provideEventRepository(context))
}
