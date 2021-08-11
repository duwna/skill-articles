package ru.skillbranch.skillarticles

import android.app.Application
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import javax.inject.Inject


class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var computer: Computer

    override fun onCreate() {
        super.onCreate()
        NetworkMonitor.registerNetworkMonitor(applicationContext)

        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)
    }
}

class Processor {
    override fun toString() = "Processor"
}

class Motherboard {
    override fun toString() = "Motherboard"
}

class RAM {
    override fun toString() = "RAM"
}

data class Computer(
    val processor: Processor,
    val motherboard: Motherboard,
    val ram: RAM
)

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: App)
    fun computer(): Computer
}

@Module
class AppModule {
    @Provides
    fun provideComputer(
        processor: Processor,
        ram: RAM,
        motherboard: Motherboard
    ): Computer {
        return Computer(
            processor = processor,
            ram = ram,
            motherboard = motherboard
        )
    }

    @Provides
    fun provideProcessor() = Processor()

    @Provides
    fun provideRam() = RAM()

    @Provides
    fun provideMotherboard() = Motherboard()
}