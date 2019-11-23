package com.practice.myapplication.mvi.di.main

import androidx.lifecycle.ViewModel
import com.practice.myapplication.mvi.di.ViewModelKey
import com.practice.myapplication.mvi.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AccountViewModel): ViewModel


}