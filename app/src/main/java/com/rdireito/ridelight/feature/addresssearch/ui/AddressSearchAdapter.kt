package com.rdireito.ridelight.feature.addresssearch.ui

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rdireito.ridelight.R
import com.rdireito.ridelight.common.ui.BaseRecyclerAdapter
import com.rdireito.ridelight.common.ui.BaseViewHolder
import com.rdireito.ridelight.data.model.Address
import kotlinx.android.synthetic.main.item_address_search.view.*

class AddressSearchAdapter(
    private val onClick: (Address, Int) -> Unit
) : BaseRecyclerAdapter<Address>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        with(inflateView(parent, LAYOUT_ID)) {
            ViewHolder(this, onClick)
        }

    override fun onBindViewHolder(holder: BaseViewHolder<Address>, position: Int) {
        holder.bind(get(position))
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes layoutId: Int): View = LayoutInflater
        .from(parent.context)
        .inflate(layoutId, parent, false)

    inner class ViewHolder(
        private val view: View,
        private val onClick: (Address, Int) -> Unit
    ) : BaseViewHolder<Address>(view) {

        override fun bind(element: Address) {
            listener(element)
            name(element.name)
            address(element.address)
        }

        private fun listener(item: Address) {
            itemView.setOnClickListener { onClick(item, adapterPosition) }
        }

        private fun name(text: String) {
            itemView.itemAddressSearchName.text = text
        }

        private fun address(text: String) {
            itemView.itemAddressSearchAddress.text = text
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.item_address_search
    }

}
