package com.practice.myapplication.mvi.di

import androidx.lifecycle.ViewModelProvider
import com.practice.myapplication.mvi.viewmodel.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
@Binds
abstract fun bindViewModelFactory(factory :ViewModelProviderFactory) :ViewModelProvider.Factory
}