/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.gemini.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.skydoves.gemini.R
import com.skydoves.gemini.core.designsystem.component.GeminiSmallTopBar
import com.skydoves.gemini.core.navigation.AppComposeNavigator
import com.skydoves.gemini.core.navigation.GeminiScreens
import com.skydoves.gemini.core.navigation.GeminiScreens.Companion.argument_channel_id
import com.skydoves.gemini.feature.channels.GeminiChannels
import com.skydoves.gemini.feature.chat.GeminiChat

fun NavGraphBuilder.geminiHomeNavigation(
  composeNavigator: AppComposeNavigator
) {
  composable(route = GeminiScreens.Channels.name) {
    Scaffold(topBar = {
      GeminiSmallTopBar(
        title = stringResource(id = R.string.app_name)
      )
    }) { padding ->
      GeminiChannels(
        modifier = Modifier.padding(padding),
        composeNavigator = composeNavigator
      )
    }
  }

  composable(
    route = GeminiScreens.Messages.name,
    arguments = GeminiScreens.Messages.navArguments
  ) {
    val channelId = it.arguments?.getString(argument_channel_id) ?: return@composable
    GeminiChat(
      channelId = channelId,
      composeNavigator = composeNavigator
    )
  }
}
