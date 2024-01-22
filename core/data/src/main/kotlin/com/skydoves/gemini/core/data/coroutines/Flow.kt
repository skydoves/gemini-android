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

package com.skydoves.gemini.core.data.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

context(ViewModel)
public fun <T> Flow<T>.asStateFlow(
  initialValue: T,
  started: SharingStarted = WhileSubscribedOrRetained
): StateFlow<T> {
  return stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = initialValue
  )
}

context(ViewModel)
public fun <T> Flow<T>.asSharedFlow(replay: Int = 0): SharedFlow<T> {
  return shareIn(
    scope = viewModelScope,
    started = WhileSubscribedOrRetained,
    replay = replay
  )
}

public fun <T> publishedFlow(
  replay: Int = 1,
  extraBufferCapacity: Int = 2
): MutableSharedFlow<T> {
  return MutableSharedFlow(
    replay = replay,
    extraBufferCapacity = extraBufferCapacity,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
}
