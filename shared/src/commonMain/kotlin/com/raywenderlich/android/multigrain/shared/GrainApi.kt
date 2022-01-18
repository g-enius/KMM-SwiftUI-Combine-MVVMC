/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.multigrain.shared

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class GrainApi(private val context: Controller) {

  private val apiUrl = "https://gist.githubusercontent.com/jblorenzo/f8b2777c217e6a77694d74e44ed6b66b/raw/0dc3e572a44b7fef0d611da32c74b187b189664a/gistfile1.txt"

  fun getGrainList(success: (List<Grain>) -> Unit, failure: (Throwable?) -> Unit) {
    GlobalScope.launch(ApplicationDispatcher) {
      try {
        val url = apiUrl
        val json = HttpClient().get<String>(url)
        Json.decodeFromString(GrainList.serializer(), json)
            .entries
            .also(success)
      } catch (ex: Exception) {
        failure(ex)
      }
    }
  }

  fun getImage(url: String, success: (Image?) -> Unit, failure: (Throwable?) -> Unit) {
    GlobalScope.launch(ApplicationDispatcher) {
      try {
        HttpClient().get<ByteArray>(url)
            .toNativeImage()
            .also(success)
      } catch (ex: Exception) {
        failure(ex)
      }
    }
  }

  fun isFavorite(id: Int): Boolean {
    return context.getBool("grain_$id")
  }

  fun setFavorite(id: Int, value: Boolean) {
    context.setBool("grain_$id", value)
  }
}