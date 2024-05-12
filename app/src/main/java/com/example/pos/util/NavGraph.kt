package com.example.pos.util

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.findNavController

object NavGraph {
    fun navigateFragment(
        view: View,
        id: Int,
        navOptions: NavOptions? = null,
        bundle: Bundle? = null
    ) {
        view.findNavController().navigate(id, bundle, navOptions ?: WidgetUtil.getNavOptions())
    }
}