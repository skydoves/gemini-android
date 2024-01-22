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

import com.skydoves.gemini.core.data.chat.STREAM_CHANNEL_MODEL
import com.skydoves.gemini.core.data.chat.STREAM_RANDOM_CHANNEL_KEY
import com.skydoves.gemini.core.data.chat.createRandomGeminiChannel
import com.skydoves.gemini.core.data.chat.geminiUser
import com.skydoves.gemini.core.database.GeminiDao
import com.skydoves.gemini.core.database.toEntity
import com.skydoves.gemini.core.datastore.PreferenceDataStore
import com.skydoves.gemini.core.model.GeminiChannel
import com.skydoves.gemini.core.network.service.ChannelService
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnSuccess
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import io.getstream.result.Result
import io.getstream.result.onSuccessSuspend
import java.util.Random
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * The implementation of the [ChannelRepository]. This class shouldn't be exposed to out of the `core-data` module.
 */
internal class ChannelRepositoryImpl @Inject constructor(
  private val chatClient: ChatClient,
  private val service: ChannelService,
  private val geminiDao: GeminiDao,
  private val preferenceDataStore: PreferenceDataStore
) : ChannelRepository {

  override suspend fun joinDefaultChannels(user: User): ApiResponse<List<GeminiChannel>> {
    val response = service.geminiChannels()
      .suspendOnSuccess {
        data.forEach { geminiChannel ->
          val number = preferenceDataStore.userNumber.value ?: 0
          val channelClient = chatClient.channel(geminiChannel.id + number)
          channelClient.create(
            memberIds = listOf(geminiUser.id, user.id),
            extraData = mapOf(
              "name" to geminiChannel.name,
              "image" to geminiUser.image,
              STREAM_CHANNEL_MODEL to geminiChannel,
              STREAM_RANDOM_CHANNEL_KEY to geminiChannel.key
            )
          ).await().onSuccessSuspend {
            geminiDao.insertGeminiChannel(geminiChannel.toEntity())
          }
        }
      }
    return response
  }

  override suspend fun createRandomChannel(user: User): Result<Channel> {
    val number = Random().nextInt(99999)
    val geminiChannel = createRandomGeminiChannel(number = number)
    return chatClient.createChannel(
      channelType = "messaging",
      channelId = UUID.randomUUID().toString(),
      memberIds = listOf(user.id, geminiUser.id),
      extraData = mapOf(
        "name" to "Gemini-Chat$number",
        "image" to "https://picsum.photos/id/${Random().nextInt(1000)}/300/300",
        STREAM_CHANNEL_MODEL to geminiChannel,
        STREAM_RANDOM_CHANNEL_KEY to "random$number"
      )
    ).await().onSuccessSuspend {
      geminiDao.insertGeminiChannel(geminiChannel.toEntity())
    }
  }

  override fun streamInitializationFlow(): Flow<InitializationState> =
    chatClient.clientState.initializationState

  override fun streamUserFlow(): Flow<User> = chatClient.clientState.user.mapNotNull { it }

  override fun shouldDisplayBalloon(): Flow<Boolean> =
    preferenceDataStore.shouldDisplayBalloon.map {
      it ?: false
    }

  override suspend fun markBalloonDisplayed() {
    preferenceDataStore.markBalloonDisplayed()
  }
}
