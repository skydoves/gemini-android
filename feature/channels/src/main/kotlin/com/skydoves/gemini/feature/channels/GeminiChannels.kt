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

package com.skydoves.gemini.feature.channels

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.balloon.compose.Balloon
import com.skydoves.gemini.core.data.chat.STREAM_RANDOM_CHANNEL_KEY
import com.skydoves.gemini.core.designsystem.component.LoadingIndicator
import com.skydoves.gemini.core.designsystem.composition.LocalOnFinishDispatcher
import com.skydoves.gemini.core.designsystem.theme.GeminiComposeTheme
import com.skydoves.gemini.core.designsystem.theme.STREAM_PRIMARY
import com.skydoves.gemini.core.navigation.AppComposeNavigator
import com.skydoves.gemini.core.navigation.GeminiScreens
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen

@Composable
fun GeminiChannels(
  modifier: Modifier,
  composeNavigator: AppComposeNavigator,
  viewModel: ChannelViewModel = hiltViewModel()
) {
  val onFinishDispatcher = LocalOnFinishDispatcher.current
  val uiState by viewModel.channelUiState.collectAsState(ChannelUiState.Idle)
  val isInitialized by viewModel.isInitialized.collectAsStateWithLifecycle(initialValue = false)

  handleUiState(uiState = uiState)

  LaunchedEffect(key1 = isInitialized) {
    if (isInitialized && uiState == ChannelUiState.Idle) {
      viewModel.handleEvent(ChannelEvent.JoinDefaultChannels)
    }
  }

  GeminiComposeTheme {
    Box(modifier = modifier.fillMaxSize()) {
      if (isInitialized) {
        ChannelsScreen(
          isShowingHeader = false,
          isShowingSearch = true,
          onItemClick = { channel ->
            val key = channel.extraData[STREAM_RANDOM_CHANNEL_KEY].toString()
            composeNavigator.navigate(
              GeminiScreens.Messages.createRoute(
                channelId = channel.cid,
                channelKey = key
              )
            )
          },
          onBackPressed = { onFinishDispatcher?.invoke() }
        )

        val shouldDisplayBalloon by viewModel.preferenceDataStore.collectAsState()
        Balloon(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .size(58.dp),
          builder = rememberFloatingBalloon(),
          balloonContent = {
            Text(
              modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
              text = stringResource(
                id = com.skydoves.gemini.core.designsystem.R.string.message_balloon
              ),
              textAlign = TextAlign.Center,
              color = Color.White
            )
          }
        ) { balloonWindow ->
          DisposableEffect(key1 = Unit) {
            if (!shouldDisplayBalloon) {
              balloonWindow.showAlignTop()
            }

            balloonWindow.setOnBalloonDismissListener {
              viewModel.handleEvent(ChannelEvent.MarkBalloonDisplayed)
              balloonWindow.dismiss()
            }

            onDispose { viewModel.handleEvent(ChannelEvent.MarkBalloonDisplayed) }
          }

          FloatingActionButton(
            modifier = Modifier.matchParentSize(),
            containerColor = STREAM_PRIMARY,
            shape = CircleShape,
            onClick = { viewModel.handleEvent(ChannelEvent.CreateRandomChannel) }
          ) {
            Icon(
              imageVector = Icons.Filled.AddComment,
              contentDescription = null,
              tint = Color.White
            )
          }
        }
      } else {
        LoadingIndicator()
      }
    }
  }
}

@Composable
private fun handleUiState(
  uiState: ChannelUiState
) {
  val context = LocalContext.current
  LaunchedEffect(key1 = uiState) {
    when (uiState) {
      is ChannelUiState.Error -> Toast.makeText(context, uiState.reason, Toast.LENGTH_SHORT).show()
      else -> Unit
    }
  }
}
