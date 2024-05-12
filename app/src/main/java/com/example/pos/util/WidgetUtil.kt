package com.example.pos.util

import androidx.navigation.NavOptions
import com.example.pos.R


object WidgetUtil {
    fun getNavOptions(
        enterAnim: Int = R.anim.enter_from_right,
        exitAnim: Int = R.anim.exit_to_left,
        popEnterAnim: Int = R.anim.enter_from_left,
        popExitAnim: Int = R.anim.exit_to_right
    ): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .setPopEnterAnim(popEnterAnim)
            .setPopExitAnim(popExitAnim)
            .build()
    }
}