package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.annotation.StringRes
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.navigation.Route

enum class MainTab(
    @StringRes val labelRes: Int,
    val route: Route,
) {
    FEED(R.string.tab_feed, Feed),
    SEARCH(R.string.tab_search, Search),
    COORDI(R.string.tab_coordi, ColleagueSelect),
    MY_PROFILE(R.string.tab_my_profile, MyProfile),
}
