package com.practice.myapplication.mvi.di.main

import com.practice.myapplication.mvi.ui.main.account.AccountFragment
import com.practice.myapplication.mvi.ui.main.account.ChangePasswordFragment
import com.practice.myapplication.mvi.ui.main.account.UpdateAccountFragment
import com.practice.myapplication.mvi.ui.main.blog.BlogFragment
import com.practice.myapplication.mvi.ui.main.blog.UpdateBlogFragment
import com.practice.myapplication.mvi.ui.main.blog.ViewBlogFragment
import com.practice.myapplication.mvi.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}