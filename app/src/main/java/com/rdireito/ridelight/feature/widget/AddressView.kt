package com.rdireito.ridelight.feature.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.taxis99.R

class AddressView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

  private val DEPARTURE_ADDRESS = 0

  val addressLayout by lazy { findViewById(R.id.address_layout) as LinearLayout }
  val addressIcon by lazy { findViewById(R.id.address_icon_image) as ImageView }
  val addressTitle by lazy { findViewById(R.id.address_title_text) as TextView }
  val addressBody by lazy { findViewById(R.id.address_body_text) as TextView }
  val addressClearButton by lazy { findViewById(R.id.address_clear_layout) as LinearLayout }

  private var isErasable = false

  init {
    inflate(context, R.layout.view_address, this)

    val styledAttrs = context.obtainStyledAttributes(attributeSet, R.styleable.AddressView, 0, 0)

    try {
      val type = styledAttrs.getInteger(R.styleable.AddressView_addressType, DEPARTURE_ADDRESS)
      setAddressTypeLayout(type)

      val hint = styledAttrs.getString(R.styleable.AddressView_addressHint)
      setAddressHint(hint)

      val erasable = styledAttrs.getBoolean(R.styleable.AddressView_addressErasable, false)
      setAddressErasable(erasable)

      gravity = Gravity.CENTER
    } finally {
      styledAttrs.recycle()
    }
  }

  fun setAddressBodyClickListener(listener: () -> Unit) {
    addressLayout.setOnClickListener { listener.invoke() }
  }

  fun setAddressClearClickListener(listener: () -> Unit) {
    addressClearButton.setOnClickListener { listener.invoke() }
  }

  private fun setAddressTypeLayout(type: Int) {
    when (type) {
      DEPARTURE_ADDRESS -> setAddressStyle(R.drawable.ic_pickup, R.string.departure, R.color.hintColor)
      else -> setAddressStyle(R.drawable.ic_destination, R.string.destination, R.color.light_blue)
    }
  }

  private fun setAddressStyle(iconRes: Int, titleRes: Int, hintColorRes: Int) {
    addressIcon.setImageResource(iconRes)
    addressTitle.text = resources.getText(titleRes)
    addressBody.setHintTextColor(ContextCompat.getColor(context, hintColorRes))

    updateLayout()
  }

  private fun setAddressHint(hint: String?) {
    hint?.let {
      addressBody.hint = it
    }

    updateLayout()
  }

  private fun setAddressErasable(erasable: Boolean) {
    isErasable = erasable
  }

  private fun updateLayout() {
    invalidate()
    requestLayout()
  }

  fun setAddressText(address: String?) {
    when (address.isNullOrBlank()) {
      true -> {
        addressBody.text = ""
        if (isErasable) {
          addressClearButton.visibility = View.GONE
        }
      }
      else -> {
        addressBody.text = address
        if (isErasable) {
          addressClearButton.visibility = View.VISIBLE
        }
      }
    }

    updateLayout()
  }
}
