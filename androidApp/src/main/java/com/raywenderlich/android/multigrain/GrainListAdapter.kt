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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.multigrain.shared.GrainApi

typealias Entry = com.raywenderlich.android.multigrain.shared.Grain

class GrainListAdapter(private val api: GrainApi) :
    RecyclerView.Adapter<GrainListAdapter.Holder>() {

  private val grainList: ArrayList<Entry> = arrayListOf()

  var onClick: (Entry, Int) -> Unit = { _, _ -> }
  var handleError: (Throwable?) -> Unit = {}

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return LayoutInflater.from(parent.context)
        .inflate(R.layout.item_grain, parent, false)
        .let { Holder(it as ViewGroup) }
  }

  override fun getItemCount(): Int = grainList.count()

  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.bind(grainList[position])
  }

  fun updateData(list: List<Entry>) {
    grainList.clear()
    grainList.addAll(list)
    notifyDataSetChanged()
  }

  inner class Holder(private val view: ViewGroup) : RecyclerView.ViewHolder(view), View
  .OnClickListener {

    private var textView: TextView = view.findViewById(R.id.textView)
    private var imageView: ImageView = view.findViewById(R.id.imageView)

    init {
      view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
      onClick(grainList[adapterPosition], adapterPosition)
    }

    fun bind(item: Entry) {
      val isFavorite = api.isFavorite(item.id)
      textView.setCompoundDrawablesWithIntrinsicBounds(
          null,
          null,
          if (isFavorite) ContextCompat.getDrawable(view.context, android.R.drawable.star_big_on)
          else null,
          null
      )

      textView.text = item.name
      item.url?.let { imageUrl ->
        api.getImage(imageUrl, { image ->
          imageView.setImageBitmap(image)
        }, {
          handleError(it)
        })
      }
    }
  }
}