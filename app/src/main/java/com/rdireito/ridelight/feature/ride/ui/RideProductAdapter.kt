package com.rdireito.ridelight.feature.ride.ui

import android.support.annotation.LayoutRes
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.ui.BaseRecyclerAdapter
import com.rdireito.ridelight.common.ui.BaseViewHolder
import com.rdireito.ridelight.data.model.Estimate
import kotlinx.android.synthetic.main.item_ride_product.view.*

class RideProductAdapter(
    private val onClick: (Estimate, Int) -> Unit
) : BaseRecyclerAdapter<Estimate>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        with(inflateView(parent, LAYOUT_ID)) {
            ViewHolder(this, onClick)
        }

    override fun onBindViewHolder(holder: BaseViewHolder<Estimate>, position: Int) {
        holder.bind(get(position))
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes layoutId: Int): View = LayoutInflater
        .from(parent.context)
        .inflate(layoutId, parent, false)

    inner class ViewHolder(
        private val view: View,
        private val onClick: (Estimate, Int) -> Unit
    ) : BaseViewHolder<Estimate>(view) {

        override fun bind(element: Estimate) {
            listener(element)
            name(element.vehicleType.name)
            icon(element.vehicleType.icons.regular)
        }

        private fun listener(item: Estimate) {
            itemView.setOnClickListener { onClick.invoke(item, adapterPosition) }
        }

        private fun name(text: String) {
            itemView.productName.text = text
        }

        private fun icon(url: String?) {
            val errorImage = AppCompatResources.getDrawable(itemView.context, R.drawable.ic_directions_car_black_24dp)
            if (url.isNullOrBlank()) {
                itemView.productIcon.setImageDrawable(errorImage)
            } else {
                Glide.with(view)
                    .load(url)
                    .apply(RequestOptions().error(errorImage))
                    .into(itemView.productIcon)
            }
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.item_ride_product
    }

}
