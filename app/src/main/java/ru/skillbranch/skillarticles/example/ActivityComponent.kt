package ru.skillbranch.skillarticles.example

import dagger.Component

@Component(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(testActivity: TestActivity)
}