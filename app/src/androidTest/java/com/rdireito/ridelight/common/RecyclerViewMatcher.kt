package com.rdireito.ridelight.common

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object RecyclerViewMatchers {
    fun withRecyclerView(recyclerViewId: Int) = RecyclerViewMatcher(recyclerViewId)
}

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    var position: Int = 0

    fun atPosition(position: Int) = apply {
        this.position = position
    }

    fun onView(targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            internal var resources: Resources? = null
            internal var childView: View? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    idDescription = try {
                        this.resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format("%s (resource name not found)", Integer.valueOf(recyclerViewId))
                    }
                }

                description.appendText("with id: " + idDescription)
            }

            override fun matchesSafely(view: View): Boolean {
                this.resources = view.resources

                if (childView == null) {
                    val recyclerView = view.rootView.findViewById(recyclerViewId) as RecyclerView?
                    if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView
                    } else {
                        return false
                    }
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView?.findViewById<View>(targetViewId)
                    view === targetView
                }

            }
        }
    }
}
