package com.minikode.summit.repository

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.minikode.summit.App

class LocationRepository {

//    private var location: Location? = null

//    private val successLocationLambda: (Location?) -> Unit = {
//        it?.let {
//            Timber.d("it latitude: ${it.latitude}")
//            Timber.d("it longitude: ${it.longitude}")
//            location = it
//        }
//    }

    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    fun getFusedLocationProviderClient(successLocationLambda: (Location?) -> Unit): FusedLocationProviderClient {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    successLocationLambda(location)
                }
            }
        }

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

//    fun getLocation() = location
}