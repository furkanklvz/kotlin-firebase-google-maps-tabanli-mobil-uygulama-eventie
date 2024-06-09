package com.example.gezirehberi

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

interface UpdateMarkersListener {
    fun updateMarkers(markerLocations: MutableList<GeoPoint>)
}