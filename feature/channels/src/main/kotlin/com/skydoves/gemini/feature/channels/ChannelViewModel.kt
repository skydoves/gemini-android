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

import androidx.lifecycle.ViewModel
import com.skydoves.gemini.core.data.coroutines.asStateFlow
import com.skydoves.gemini.core.data.coroutines.publishedFlow
import com.skydoves.gemini.core.data.repository.ChannelRepository
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.messageOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.models.InitializationState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ChannelViewModel @Inject constructor(
  private val repository: ChannelRepository
) : ViewModel() {

  private val channelEvent: MutableSharedFlow<ChannelEvent> = publishedFlow()
  internal val isInitialized = repository.streamInitializationFlow().mapLatest {
    it == InitializationState.COMPLETE
  }
  internal val preferenceDataStore = repository.shouldDisplayBalloon().asStateFlow(false)

  private val userFlow = repository.streamUserFlow()
  internal val channelUiState: SharedFlow<ChannelUiState> =
    combine(channelEvent, userFlow) { event, user ->
      event to user
    }.flatMapLatest { pair ->
      val (event, user) = pair
      when (event) {
        is ChannelEvent.JoinDefaultChannels -> {
          val response = repository.joinDefaultChannels(user = user)
          if (response.isSuccess) {
            flowOf(ChannelUiState.JoinSuccess)
          } else {
            flowOf(ChannelUiState.Error(response.messageOrNull))
          }
        }

        is ChannelEvent.CreateRandomChannel -> {
          val result = repository.createRandomChannel(user = user)
          if (result.isSuccess) {
            flowOf(ChannelUiState.CreateSuccess)
          } else {
            flowOf(ChannelUiState.Error(result.errorOrNull()?.message))
          }
        }

        is ChannelEvent.MarkBalloonDisplayed -> {
          repository.markBalloonDisplayed()
          flowOf(ChannelUiState.Idle)
        }
      }
    }.asStateFlow(ChannelUiState.Idle)

  internal fun handleEvent(event: ChannelEvent) {
    channelEvent.tryEmit(event)
  }
}

sealed interface ChannelEvent {

  data object JoinDefaultChannels : ChannelEvent

  data object CreateRandomChannel : ChannelEvent

  data object MarkBalloonDisplayed : ChannelEvent
}

sealed interface ChannelUiState {

  data object Idle : ChannelUiState

  data object JoinSuccess : ChannelUiState

  data object CreateSuccess : ChannelUiState

  data class Error(val reason: String?) : ChannelUiState
}
