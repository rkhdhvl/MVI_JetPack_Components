package com.practice.myapplication.mvi.di.main

import androidx.lifecycle.ViewModel
import com.practice.myapplication.mvi.di.ViewModelKey
import com.practice.myapplication.mvi.ui.main.account.AccountViewModel
import com.practice.myapplication.mvi.ui.main.blog.viewmodel.BlogViewModel
import com.practice.myapplication.mvi.ui.main.create_blog.viewmodel.CreateBlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(blogViewModel: CreateBlogViewModel): ViewModel


}