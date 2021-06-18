package com.lenatopoleva.map.view.main

import androidx.lifecycle.ViewModel
import com.lenatopoleva.map.navigation.Screens
import ru.terrakok.cicerone.Router


class MainActivityViewModel (private val router: Router): ViewModel(){

    fun backPressed() {
        router.exit()
    }

    fun onCreate() {
        router.replaceScreen(Screens.MapScreen())
    }

}