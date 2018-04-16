package com.rdireito.ridelight.feature.ride.ui.map

import android.content.Context
import android.util.SparseArray
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.rdireito.ridelight.R


class RideMapFragment : SupportMapFragment() {

    private val spacing by lazy { resources.getDimensionPixelSize(R.dimen.spacing_normal) }
    private val markers: SparseArray<Marker> = SparseArray()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        withMap {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        }
    }

    fun moveMapToLocation(latitude: Double, longitude: Double) {
        withMap {
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(DEFAULT_ZOOM)
                .build()
            animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    fun addMarker(latitude: Double, longitude: Double, rideMarker: RideMarker) {
        withMap {
            markers[rideMarker.ordinal]?.remove()

            val marker =
                addMarker(
                    MarkerOptions()
                        .position(LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(rideMarker.color))
                )

            markers.put(rideMarker.ordinal, marker)
        }
    }

    fun setMapPadding(height: Int) {
        withMap {
            setPadding(spacing, spacing * 5, spacing, height + spacing)
        }
    }

    private fun withMap(action: GoogleMap.() -> Unit) {
        getMapAsync { it.action() }
    }

    enum class RideMarker(val color: Float) {
        PICKUP(BitmapDescriptorFactory.HUE_GREEN),
        DROPOFF(BitmapDescriptorFactory.HUE_AZURE)
    }

    companion object {
        val TAG: String = RideMapFragment::class.java.canonicalName
        private const val DEFAULT_ZOOM = 17f

        fun newInstance() = RideMapFragment()
    }

}
