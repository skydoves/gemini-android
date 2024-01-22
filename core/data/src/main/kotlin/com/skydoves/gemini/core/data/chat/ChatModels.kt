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

package com.skydoves.gemini.core.data.chat

import com.skydoves.gemini.core.model.GeminiChannel
import com.skydoves.gemini.core.model.GeminiModel
import io.getstream.chat.android.models.User

public val geminiUser = User(
  id = "gemini",
  role = "admin",
  name = "gemini",
  image = "https://avatars.githubusercontent.com/u/8597527?s=200&v=4.png"
)

public const val STREAM_CHANNEL_MODEL: String = "channelModel"

public const val STREAM_CHANNEL_GEMINI_FLAG = "Gemini"

public const val STREAM_RANDOM_CHANNEL_KEY = "key"

public fun createRandomGeminiChannel(number: Int): GeminiChannel {
  return GeminiChannel(
    id = "messaging:random-chat$number",
    name = "Gemini-Chat$number",
    key = "random$number",
    model = GeminiModel(
      name = "gemini-pro",
      temperature = 0.5f,
      candidateCount = 1,
      maxOutputTokens = 500,
      topK = 30,
      topP = 0.5f
    )
  )
}
