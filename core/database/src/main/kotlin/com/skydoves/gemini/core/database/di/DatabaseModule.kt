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

package com.skydoves.gemini.core.database.di

import android.app.Application
import androidx.room.Room
import com.skydoves.gemini.core.database.GeminiDao
import com.skydoves.gemini.core.database.GeminiDatabase
import com.skydoves.gemini.core.database.GeminiModelConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

  @Provides
  @Singleton
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()
  }

  @Provides
  @Singleton
  fun provideAppDatabase(
    application: Application,
    geminiModelConverter: GeminiModelConverter
  ): GeminiDatabase {
    return Room
      .databaseBuilder(application, GeminiDatabase::class.java, "Gemini.db")
      .fallbackToDestructiveMigration()
      .addTypeConverter(geminiModelConverter)
      .build()
  }

  @Provides
  @Singleton
  fun provideGeminiDao(appDatabase: GeminiDatabase): GeminiDao {
    return appDatabase.geminiDao()
  }
}
