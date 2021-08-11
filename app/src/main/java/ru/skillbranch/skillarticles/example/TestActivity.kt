package ru.skillbranch.skillarticles.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.log
import javax.inject.Inject

class TestActivity : AppCompatActivity() {

    @Inject
    lateinit var injectPair: Pair<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_3)

        val component = DaggerActivityComponent.builder()
            .activityModule(ActivityModule())
            .build()

        component.inject(this)

        log(injectPair)
    }
}