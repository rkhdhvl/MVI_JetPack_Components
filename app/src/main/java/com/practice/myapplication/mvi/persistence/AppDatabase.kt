package com.practice.myapplication.mvi.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingwithmitch.openapi.models.AccountProperties
import com.practice.myapplication.mvi.models.AuthToken
import com.practice.myapplication.mvi.models.BlogPost
import com.practice.myapplication.mvi.persistence.AccountPropertiesDao
import com.practice.myapplication.mvi.persistence.AuthTokenDao

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}








