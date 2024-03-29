package com.practice.myapplication.mvi.di

import com.codingwithmitch.openapi.di.auth.AuthScope
import com.practice.myapplication.mvi.di.auth.AuthFragmentBuildersModule
import com.practice.myapplication.mvi.di.auth.AuthModule
import com.practice.myapplication.mvi.di.auth.AuthViewModelModule
import com.practice.myapplication.mvi.di.main.MainFragmentBuildersModule
import com.practice.myapplication.mvi.di.main.MainModule
import com.practice.myapplication.mvi.di.main.MainScope
import com.practice.myapplication.mvi.di.main.MainViewModelModule
import com.practice.myapplication.mvi.ui.auth.AuthActivity
import com.practice.myapplication.mvi.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )abstract fun contributAuthActivity() : AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}