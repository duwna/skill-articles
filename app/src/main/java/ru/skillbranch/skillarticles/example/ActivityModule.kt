package ru.skillbranch.skillarticles.example

import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    @Provides
    fun providePair(): Pair<String, String> = "inject" to "pair"
}