/**
 * Copyright 2017 Google Inc. All Rights Reserved.
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
package org.baole.oned.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import org.baole.oned.R
import org.baole.oned.model.Restaurant
import org.baole.oned.util.RestaurantUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class RestaurantAdapter(query: Query, private val mListener: OnRestaurantSelectedListener) : FirestoreAdapter<RestaurantViewHolder>(query) {

    interface OnRestaurantSelectedListener {

        fun onRestaurantSelected(restaurant: DocumentSnapshot)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RestaurantViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }


}

class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imageView: ImageView
    var nameView: TextView
    var ratingBar: MaterialRatingBar
    var numRatingsView: TextView
    var priceView: TextView
    var categoryView: TextView
    var cityView: TextView

    init {
        imageView = itemView.findViewById(R.id.restaurant_item_image)
        nameView = itemView.findViewById(R.id.restaurant_item_name)
        ratingBar = itemView.findViewById(R.id.restaurant_item_rating)
        numRatingsView = itemView.findViewById(R.id.restaurant_item_num_ratings)
        priceView = itemView.findViewById(R.id.restaurant_item_price)
        categoryView = itemView.findViewById(R.id.restaurant_item_category)
        cityView = itemView.findViewById(R.id.restaurant_item_city)
    }

    fun bind(snapshot: DocumentSnapshot,
             listener: RestaurantAdapter.OnRestaurantSelectedListener?) {

        val restaurant = snapshot.toObject(Restaurant::class.java)
        val resources = itemView.resources

        // Load image
        Glide.with(imageView.context)
                .load(restaurant!!.photo)
                .into(imageView)

        nameView.text = restaurant.name
        ratingBar.rating = restaurant.avgRating.toFloat()
        cityView.text = restaurant.city
        categoryView.text = restaurant.category
        numRatingsView.text = resources.getString(R.string.fmt_num_ratings,
                restaurant.numRatings)
        priceView.text = RestaurantUtil.getPriceString(restaurant)

        // Click listener
        itemView.setOnClickListener {
            listener?.onRestaurantSelected(snapshot)
        }
    }

}
