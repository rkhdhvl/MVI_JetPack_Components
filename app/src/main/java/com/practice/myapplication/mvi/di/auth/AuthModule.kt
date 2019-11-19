package com.practice.myapplication.mvi.di.auth

import android.content.SharedPreferences
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.practice.myapplication.mvi.api.auth.OpenApiAuthService
import com.practice.myapplication.mvi.persistence.AccountPropertiesDao
import com.practice.myapplication.mvi.persistence.AuthTokenDao
import com.practice.myapplication.mvi.repository.auth.AuthRepository
import com.practice.myapplication.mvi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
        ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            preferences,
            editor
        )
    }

}