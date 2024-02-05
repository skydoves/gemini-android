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

package com.skydoves.gemini.feature.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.skydoves.gemini.core.data.chat.STREAM_CHANNEL_GEMINI_FLAG
import com.skydoves.gemini.core.data.coroutines.asStateFlow
import com.skydoves.gemini.core.data.repository.ChatRepository
import com.skydoves.gemini.core.data.utils.Empty
import com.skydoves.gemini.core.model.GeminiChannel
import com.skydoves.gemini.core.navigation.GeminiScreens.Companion.argument_channel_id
import com.skydoves.gemini.core.navigation.GeminiScreens.Companion.argument_channel_key
import com.skydoves.gemini.feature.chat.extension.toGenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.common.state.messages.list.MessageItemState
import io.getstream.chat.android.ui.common.state.messages.list.MessageListItemState
import io.getstream.log.streamLog
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
  repository: ChatRepository,
  chatClient: ChatClient,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val channelId: String = savedStateHandle.get<String>(argument_channel_id) ?: String.Empty
  private val channelKey: String =
    savedStateHandle.get<String>(argument_channel_key) ?: String.Empty
  private val channelClient = chatClient.channel(channelId)

  private val geminiChannelFlow: Flow<GeminiChannel> = repository.getGeminiChannel(channelKey)

  private val generativeModel: StateFlow<GenerativeModel?> =
    geminiChannelFlow.mapLatest { it.model.toGenerativeModel() }.asStateFlow(null)

  private val generativeChat: StateFlow<Chat?> =
    generativeModel.mapLatest { it?.startChat() }.asStateFlow(null)

  private val messageItemSet = MutableStateFlow<Set<String>>(setOf())
  val isLoading: StateFlow<Boolean> =
    combine(messageItemSet, generativeChat) { messageItemSet, chat ->
      messageItemSet.isNotEmpty() || chat == null
    }.asStateFlow(false)

  private val mutableError: MutableStateFlow<String> = MutableStateFlow(String.Empty)
  val errorMessage: StateFlow<String> = mutableError
    .filter { it.isNotEmpty() }
    .asStateFlow(String.Empty)

  val isMessageEmpty: StateFlow<Boolean> = repository
    .watchIsChannelMessageEmpty(channelId).asStateFlow(false)

  fun addHistories(messages: List<MessageListItemState>) {
    val history = generativeChat.value?.history
    if (history?.isEmpty() == true) {
      messages.filterIsInstance(MessageItemState::class.java).forEach { messageState ->
        val content = if (messageState.message.extraData[STREAM_CHANNEL_GEMINI_FLAG] == true) {
          content(role = "model") { text(messageState.message.text) }
        } else {
          content(role = "user") { text(messageState.message.text) }
        }
        history.add(content)
      }
    }
  }

  fun sendStreamChatMessage(text: String) {
    viewModelScope.launch {
      channelClient.sendMessage(
        message = Message(
          id = UUID.randomUUID().toString(),
          cid = channelClient.cid,
          text = text,
          extraData = mutableMapOf(STREAM_CHANNEL_GEMINI_FLAG to true)
        )
      ).await()
    }
  }

  fun sendMessage(message: Message) {
    val containsAttachment = message.attachments.isNotEmpty()
    messageItemSet.value += message.text

    viewModelScope.launch {
      try {
        val responseText = if (containsAttachment) {
          val bitmaps = message.attachments.map {
            val original = BitmapFactory.decodeStream(FileInputStream(it.upload))
            Bitmap.createScaledBitmap(
              original,
              (original.width * 0.5f).toInt(),
              (original.height * 0.5f).toInt(),
              true
            )
          }
          photoReasoning(message, bitmaps)
        } else {
          sendTextMessage(message.text)
        }
        streamLog { "gemini response success: $responseText" }
        messageItemSet.value -= message.text
      } catch (e: Exception) {
        val error = e.localizedMessage.orEmpty()
        messageItemSet.value -= messageItemSet.value
        mutableError.value = error
        streamLog { "gemini response failed: $error" }
      }
    }
  }

  private suspend fun sendTextMessage(text: String): String? {
    val generativeChat = generativeChat.value ?: return null
    val response = generativeChat.sendMessage(text)
    val responseText = response.text
    if (responseText != null) {
      channelClient.sendMessage(
        message = Message(
          id = UUID.randomUUID().toString(),
          cid = channelClient.cid,
          text = responseText,
          extraData = mutableMapOf(STREAM_CHANNEL_GEMINI_FLAG to true)
        )
      ).await()
    }
    return responseText
  }

  private suspend fun photoReasoning(message: Message, bitmaps: List<Bitmap>): String? {
    val text = message.text
    val prompt = "Look at the image(s), and then answer the following question: $text"
    val generativeModel = generativeModel.value ?: return null
    val content = content {
      for (bitmap in bitmaps) {
        image(bitmap)
      }
      text(prompt)
    }
    val response = generativeModel.generateContent(content)
    val responseText = response.text
    if (responseText != null) {
      channelClient.sendMessage(
        message = Message(
          id = UUID.randomUUID().toString(),
          cid = channelClient.cid,
          text = responseText,
          extraData = mutableMapOf(STREAM_CHANNEL_GEMINI_FLAG to true)
        )
      ).await()
    }
    return responseText
  }
}
