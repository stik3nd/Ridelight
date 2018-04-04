package com.rdireito.ridelight.feature.addresssearch.ui

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rdireito.ridelight.R
import com.rdireito.ridelight.data.model.Address

class AddressSearchAdapter(
    private val list: MutableList<Address> = mutableListOf(),
    private val onClick: (Address, Int) -> Unit
) : RecyclerView.Adapter<AddressSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        with(inflateView(parent, LAYOUT_ID)) {
            ViewHolder(this, onClick)
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addAll(items: List<Address>) {
        list.addAll(items)
        notifyItemRangeInserted(list.size, items.size)
    }

    fun clear() {
        val lastSize = list.size
        if (list.size > 0) {
            list.clear()
            notifyItemRangeRemoved(0, lastSize)
        }
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes layoutId: Int): View = LayoutInflater
        .from(parent.context)
        .inflate(layoutId, parent, false)

    inner class ViewHolder(
        private val view: View,
        private val onClick: (Address, Int) -> Unit
    ) : RecyclerView.ViewHolder(view), Binder<Address> {

        private val name by lazy { itemView.findViewById<TextView>(R.id.itemAddressSearchName) }
        private val address by lazy { itemView.findViewById<TextView>(R.id.itemAddressSearchAddress) }

        override fun bind(item: Address) {
            listener(item)
            name(item.name)
            address(item.address)
        }

        private fun listener(item: Address) {
            itemView.setOnClickListener { onClick.invoke(item, adapterPosition) }
        }

        private fun name(text: String) {
            name.text = text
        }

        private fun address(text: String) {
            address.text = text
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.item_address_search
    }

    interface Binder<in T> {
        fun bind(item: T)
    }
}
