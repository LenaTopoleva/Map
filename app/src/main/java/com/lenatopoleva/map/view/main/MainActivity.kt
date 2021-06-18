package com.lenatopoleva.map.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lenatopoleva.map.R
import com.lenatopoleva.map.view.BackButtonListener
import org.koin.android.ext.android.getKoin
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class MainActivity : AppCompatActivity() {

    val navigatorHolder: NavigatorHolder by lazy { getKoin().get<NavigatorHolder>() }
    val navigator = SupportAppNavigator(this, supportFragmentManager, R.id.container)

    val model: MainActivityViewModel by lazy {
        ViewModelProvider(this, getKoin().get()).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null) model.onCreate()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackButtonListener && it.backPressed()) {
                return
            }
        }
        model.backPressed()
    }

}