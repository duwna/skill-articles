package ru.skillbranch.skillarticles.example

import dagger.Component
import ru.skillbranch.skillarticles.di.modules.components.AppComponent
import javax.inject.Singleton

@Singleton
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(testActivity: TestActivity)
}