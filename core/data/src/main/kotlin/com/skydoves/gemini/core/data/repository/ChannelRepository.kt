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

package com.skydoves.gemini.core.data.repository

import com.skydoves.gemini.core.model.GeminiChannel
import com.skydoves.sandwich.ApiResponse
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow

/** The exposed abstraction layer of the [ChannelRepositoryImpl]. */
public interface ChannelRepository {

  suspend fun joinDefaultChannels(user: User): ApiResponse<List<GeminiChannel>>

  suspend fun createRandomChannel(user: User): Result<Channel>

  fun streamInitializationFlow(): Flow<InitializationState>

  fun streamUserFlow(): Flow<User>

  fun shouldDisplayBalloon(): Flow<Boolean>

  suspend fun markBalloonDisplayed()
}
