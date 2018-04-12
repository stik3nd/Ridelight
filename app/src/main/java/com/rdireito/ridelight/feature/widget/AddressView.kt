package com.rdireito.ridelight.feature.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.util.AttributeSet
import com.rdireito.ridelight.R
import kotlinx.android.synthetic.main.view_address.view.*
import android.util.TypedValue
import android.view.View

class AddressView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val animTime by lazy { resources.getInteger(android.R.integer.config_mediumAnimTime).toLong() }

    init {
        inflate(context, R.layout.view_address, this)

        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        this.setBackgroundResource(outValue.resourceId)

        val styledAttrs = context.obtainStyledAttributes(attributeSet, R.styleable.AddressView, 0, 0)

        try {
            val indicatorText = styledAttrs.getString(R.styleable.AddressView_indicatorText)
            setIndicatorText(indicatorText)

            val indicatorIcon = styledAttrs.getDrawable(R.styleable.AddressView_indicatorIcon)
            setIndicatorIcon(indicatorIcon)
        } finally {
            styledAttrs.recycle()
        }
    }

    fun updateAddress(address: String) {
        hideView(addressBodyText) {
            addressBodyText.text = address
            showView(addressBodyText)
        }
    }

    private fun setIndicatorText(text: String) {
        addressIndicatorText.text = text
    }

    private fun setIndicatorIcon(icon: Drawable) {
        addressIndicatorIcon.setImageDrawable(icon)
    }

    private fun hideView(view: View, endAction: () -> Unit) {
        view.animate().apply {
            alpha(0f)
            interpolator = FastOutLinearInInterpolator()
            duration = animTime
            withEndAction(endAction)
            start()
        }
    }

    private fun showView(view: View) {
        view.animate().apply {
            alpha(1f)
            interpolator = FastOutLinearInInterpolator()
            duration = animTime
            start()
        }
    }

}
