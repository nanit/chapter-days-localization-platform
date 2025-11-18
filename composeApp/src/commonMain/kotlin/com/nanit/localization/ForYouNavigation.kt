package com.nanit.localization

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

// Route => Values screen
@Serializable data object ValuesScreenRoute

// Route => base navigation graph
@Serializable data object ValuesBaseRoute

fun NavController.navigateToValuesPage(navOptions: NavOptions) = navigate(route = ValuesScreenRoute, navOptions)

fun NavGraphBuilder.valuesSection(
    dest: NavGraphBuilder.() -> Unit,
) {
    navigation<ValuesBaseRoute>(startDestination = ValuesScreenRoute) {
        composable<ValuesScreenRoute> {
//            ValuesScreen()
            CrowdinLikeScreen()
        }
        dest()
    }
}
