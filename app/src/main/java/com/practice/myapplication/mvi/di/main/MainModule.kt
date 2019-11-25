package com.practice.myapplication.mvi.di.main

import com.practice.myapplication.mvi.api.main.OpenApiMainService
import com.practice.myapplication.mvi.persistence.AccountPropertiesDao
import com.practice.myapplication.mvi.persistence.AppDatabase
import com.practice.myapplication.mvi.persistence.BlogPostDao
import com.practice.myapplication.mvi.repository.main.AccountRepository
import com.practice.myapplication.mvi.repository.main.BlogRepository
import com.practice.myapplication.mvi.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideMainApiService(retrofirBuilder : Retrofit.Builder) : OpenApiMainService{
        return retrofirBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ):AccountRepository
    {
        return AccountRepository(openApiMainService,accountPropertiesDao,sessionManager)
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db : AppDatabase) : BlogPostDao{
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ):BlogRepository
    {
      return BlogRepository(openApiMainService,blogPostDao,sessionManager)
    }

}