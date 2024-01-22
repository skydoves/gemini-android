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

package com.skydoves.gemini.feature.chat.extension

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.skydoves.gemini.core.model.GeminiModel
import com.skydoves.gemini.feature.chat.BuildConfig

internal fun GeminiModel.toGenerativeModel(): GenerativeModel {
  return GenerativeModel(
    modelName = name,
    apiKey = BuildConfig.GEMINI_API_KEY,
    generationConfig = generationConfig {
      this.temperature = this@toGenerativeModel.temperature
      this.candidateCount = this@toGenerativeModel.candidateCount
      this.topK = this@toGenerativeModel.topK
      this.topP = this@toGenerativeModel.topP
    }
  )
}
