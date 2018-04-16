package com.rdireito.ridelight.feature.ride.ui

import android.support.annotation.LayoutRes
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        with(inflateView(parent, LAYOUT_ID)) {
            ViewHolder(this, onClick)
        }

    override fun onBindViewHolder(holder: BaseViewHolder<Estimate>, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(get(position))
            holder.select(selectedItemPosition == position)
        }
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes layoutId: Int): View = LayoutInflater
        .from(parent.context)
        .inflate(layoutId, parent, false)

    fun setSelected(adapterPosition: Int) {
        when (selectedItemPosition) {
            RecyclerView.NO_POSITION -> {
                selectedItemPosition = adapterPosition
                notifyItemChanged(adapterPosition)
            }
            else -> {
                val oldItemSelected = selectedItemPosition
                selectedItemPosition = adapterPosition
                notifyItemChanged(oldItemSelected)
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    inner class ViewHolder(
        private val view: View,
        private val onClick: (Estimate, Int) -> Unit
    ) : BaseViewHolder<Estimate>(view) {

        override fun bind(element: Estimate) {
            listener(element)
            name(element.vehicleType.name)
            icon(element.vehicleType.icons.regular)
            price(element.priceFormatted.orEmpty())
        }

        fun select(isSelected: Boolean) {
            view.isSelected = isSelected
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

        private fun price(priceFormatted: String) {
            if (priceFormatted.isNotBlank())
                itemView.productPrice.text = priceFormatted
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.item_ride_product
    }

}
