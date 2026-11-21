package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.navigation.Route

enum class MainTab(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val selectedIconRes: Int,
    val route: Route,
) {
    FEED(R.string.tab_feed, R.drawable.ic_main_home_24, R.drawable.ic_main_home_selected_24, Feed),
    SEARCH(R.string.tab_search, R.drawable.ic_main_search_24, R.drawable.ic_main_search_selected_24, Search),
    COORDI(R.string.tab_coordi, R.drawable.ic_main_hanger_24, R.drawable.ic_main_hanger_selected_24, ColleagueSelect),
    MY_PROFILE(R.string.tab_my_profile, R.drawable.ic_main_user_24, R.drawable.ic_main_user_selected_24, MyProfile),
}
