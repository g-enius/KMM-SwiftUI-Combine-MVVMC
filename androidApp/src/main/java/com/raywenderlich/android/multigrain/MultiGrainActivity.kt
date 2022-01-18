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

package com.raywenderlich.android.multigrain

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.multigrain.shared.GrainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MultiGrainActivity : AppCompatActivity(), CoroutineScope {

  private lateinit var job: Job
  private lateinit var api: GrainApi
  private lateinit var list: RecyclerView
  private lateinit var rootView: View

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private lateinit var grainAdapter: GrainListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_multi_grain)

    job = Job()
    api = GrainApi(this)
    list = findViewById(R.id.list)
    rootView = findViewById(R.id.root_view)
    grainAdapter = GrainListAdapter(api)

    setupRecyclerView()

    loadList()
  }

  private fun setupRecyclerView() {
    grainAdapter.onClick = { item, position ->
      toggleFavorite(item.id)
      grainAdapter.notifyItemChanged(position)
      list.scrollBy(0, 0)
    }
    grainAdapter.handleError = ::handleError

    list.apply {
      layoutManager = LinearLayoutManager(this@MultiGrainActivity)
      adapter = grainAdapter
      itemAnimator = null
      setHasFixedSize(true)
      addItemDecoration(DividerItemDecoration(this@MultiGrainActivity, LinearLayoutManager
          .VERTICAL))
    }
  }

  private fun loadList() {
    api.getGrainList(
        success = { launch(Main) { grainAdapter.updateData(it) } },
        failure = ::handleError
    )
  }

  private fun toggleFavorite(id: Int) {
    val isFavorite = api.isFavorite(id)
    api.setFavorite(id, !isFavorite)
  }

  private fun handleError(ex: Throwable?) {
    ex?.printStackTrace()
    launch(Main) {
      val msg = ex?.message ?: "Unknown error"
      Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE)
          .setAction("Retry") { loadList() }
          .show()
    }
  }

  override fun onDestroy() {
    job.cancel()
    super.onDestroy()
  }
}
