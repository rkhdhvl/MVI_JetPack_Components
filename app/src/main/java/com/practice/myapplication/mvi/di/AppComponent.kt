package com.practice.myapplication.mvi.di

import android.app.Application
import com.practice.myapplication.mvi.BaseApplication
import com.practice.myapplication.mvi.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
modules = [
AndroidInjectionModule::class,
AppModule::class,
ActivityBuildersModule::class,
ViewModelFactoryModule::class
]
)
interface AppComponent : AndroidInjector<BaseApplication> {

 val sessionManager: SessionManager  // must add here b/c injecting into abstract class

@Component.Builder
interface Builder{

 @BindsInstance
 fun application(application: Application) :Builder

 fun build() : AppComponent

}

}