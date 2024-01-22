/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
  id("skydoves.android.library")
  id("skydoves.android.library.compose")
  id("skydoves.android.hilt")
  id("skydoves.spotless")
}

android {
  namespace = "com.skydoves.gemini.core.navigation"
}

dependencies {
  implementation(project(":core:model"))

  implementation(libs.kotlinx.coroutines.android)
  api(libs.androidx.navigation.compose)
}