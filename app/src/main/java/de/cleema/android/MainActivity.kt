/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.models.DeepLinking
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.login.LoginScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDebugSettings()
        setDecorFitsSystemWindows(window, false)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
                systemUiController.setNavigationBarColor(Color.Transparent)
            }
            CleemaTheme {
                NavHost(navController = rememberNavController(), "login") {
                    composable(
                        "login",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern =
                                    "${BuildConfig.apiUrl}/${DeepLinking.accountConfirmation}?confirmation={${DeepLinking.confirmationCode}}"
                            },
                            navDeepLink {
                                uriPattern =
                                    "${BuildConfig.baseUrl}/invites/{${DeepLinking.invitationCode}}"
                            })
                    ) {
                        LoginScreen(modifier = Modifier.padding(0.dp))
                    }
                }

            }
        }
    }

    @Inject
    lateinit var userRepository: UserRepository

    private fun applyDebugSettings() {
        if (BuildConfig.DEBUG) {
            intent.extras?.getBoolean("wipeuser")?.let { wipeUser ->
                if (wipeUser) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userRepository.deleteUser()
                    }
                }
            }
        }

    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CleemaTheme {
        App()
    }
}
