package com.minikode.summit.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.minikode.summit.App

class LocationRepository {

    private lateinit var location: Location

    private val successLocationLambda: (Location?) -> Unit = {
        it?.let {
            Log.d(TAG, "it latitude: ${it.latitude}")
            Log.d(TAG, "it longitude: ${it.longitude}")
            location = it
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations) {
                successLocationLambda(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getFusedLocationProviderClient(): FusedLocationProviderClient {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(App.instance)
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(successLocationLambda)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        return fusedLocationProviderClient
    }

    fun removeLocationUpdate(fusedLocationProviderClient: FusedLocationProviderClient) {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun getLocation() = location

    companion object {
        private const val TAG = "LocationRepository"
    }
}