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

package com.skydoves.gemini.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skydoves.gemini.core.model.GeminiChannel
import com.skydoves.gemini.core.model.GeminiModel

@Entity
data class GeminiChannelEntity(
  val id: String,
  @PrimaryKey val key: String,
  val name: String,
  val model: GeminiModel
)

fun GeminiChannel.toEntity(): GeminiChannelEntity {
  return GeminiChannelEntity(
    id = id,
    key = key,
    name = name,
    model = model
  )
}

fun GeminiChannelEntity.toDomain(): GeminiChannel {
  return GeminiChannel(
    id = id,
    key = key,
    name = name,
    model = model
  )
}
