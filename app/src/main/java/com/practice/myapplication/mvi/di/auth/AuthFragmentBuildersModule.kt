package com.practice.myapplication.mvi.di.auth

import com.practice.myapplication.mvi.ui.auth.ForgotPasswordFragment
import com.practice.myapplication.mvi.ui.auth.LauncherFragment
import com.practice.myapplication.mvi.ui.auth.LoginFragment
import com.practice.myapplication.mvi.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}