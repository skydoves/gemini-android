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

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.skydoves.gemini.core.model.GeminiChannel
import kotlinx.coroutines.flow.Flow

/** The exposed abstraction layer of the [ChatRepositoryImpl]. */
public interface ChatRepository {

  suspend fun summaryContent(
    generativeModel: GenerativeModel,
    prompt: String
  ): GenerateContentResponse

  suspend fun sendMessage(chat: Chat, prompt: String): GenerateContentResponse

  suspend fun photoReasoning(
    generativeModel: GenerativeModel,
    content: Content
  ): GenerateContentResponse

  fun watchIsChannelMessageEmpty(cid: String): Flow<Boolean>

  fun getGeminiChannel(key: String): Flow<GeminiChannel>
}
