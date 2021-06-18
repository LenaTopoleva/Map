package com.lenatopoleva.map.navigation

import com.lenatopoleva.map.view.mapscreen.MapFragment
import com.lenatopoleva.map.view.markerlistscreen.MarkerListFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class Screens {
    class MapScreen() : SupportAppScreen() {
        override fun getFragment() = MapFragment.newInstance()
    }

    class MarkerListScreen(): SupportAppScreen() {
        override fun getFragment() = MarkerListFragment.newInstance()
    }
}