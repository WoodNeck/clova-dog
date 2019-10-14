package com.cidteamq.clovaproject.dashboard

import android.support.v4.app.Fragment

abstract class DashboardFragment: Fragment {
    constructor(): super()
    constructor(info: LearningInfo): super()
}
