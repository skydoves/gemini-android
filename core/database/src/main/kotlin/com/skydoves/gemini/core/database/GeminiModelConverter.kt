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

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.skydoves.gemini.core.model.GeminiModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject

@ProvidedTypeConverter
class GeminiModelConverter @Inject constructor(
  private val moshi: Moshi
) {

  @TypeConverter
  fun fromString(value: String): GeminiModel? {
    val adapter: JsonAdapter<GeminiModel> = moshi.adapter(GeminiModel::class.java)
    return adapter.fromJson(value)
  }

  @TypeConverter
  fun fromInfoType(type: GeminiModel?): String {
    val adapter: JsonAdapter<GeminiModel> = moshi.adapter(GeminiModel::class.java)
    return adapter.toJson(type)
  }
}
