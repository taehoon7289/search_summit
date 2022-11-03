package com.minikode.summit.util

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.minikode.summit.App
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

class Util {

    companion object {

        fun calBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

            val pi1 = lat1.times(Math.PI).div(180)
            val pi2 = lat2.times(Math.PI).div(180)
            val lambda1 = lon1.times(Math.PI).div(180)
            val lambda2 = lon2.times(Math.PI).div(180)

            val y = sin(lambda2.minus(lambda1)).times(Math.cos(pi2))
            val x = cos(pi1).times(sin(pi2))
                .minus((sin(pi1).times(cos(pi2).times(cos(lambda2.minus(lambda1))))))

            val theta = atan2(y, x)
            val bearing = (theta.times(180).div(Math.PI).plus(360)).mod(360.0)
//            Log.d(TAG, "calBearing: theta $theta")
            return bearing


        }

        fun calDist(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val EARTH_R = 6371000.0
            val rad = Math.PI / 180
            val radLat1 = rad * lat1
            val radLat2 = rad * lat2
            val radDist = rad * (lon1 - lon2)

            var distance = Math.sin(radLat1) * Math.sin(radLat2)
            distance += Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist)
            val ret = EARTH_R * Math.acos(distance)

            return ret.roundToLong().toDouble() // 미터 단위
        }

        val updateLocationLambda: (Location?, callback: (Location?) -> Unit) -> Unit =
            { location, callback ->
                location?.let {
                    Log.d(TAG, "updateLocationLambda2: update latitude ${it.latitude}")
                    Log.d(TAG, "updateLocationLambda2: update longitude ${it.longitude}")
                    Log.d(TAG, "updateLocationLambda2: update altitude ${it.altitude}")
                    Log.d(TAG, "updateLocationLambda2: update bearing ${it.bearing}")
                    callback(it)
                }

            }

        @SuppressLint("MissingPermission")
        fun getLocation(callback: (Location?) -> Unit) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(App.instance)
            val locationRequest = LocationRequest.create().apply {
                interval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

//            val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest)
//            val client = LocationServices.getSettingsClient(App.instance)
//            val task = client.checkLocationSettings(builder.build())
//            task.addOnSuccessListener {
//                Log.d(TAG, "location client setting success")
//            }
//
//            task.addOnFailureListener {
//                Log.d(TAG, "location client setting failure")
//            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                updateLocationLambda(it, callback)
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        for (location in p0.locations) {
                            updateLocationLambda(location, callback)
                        }
                    }
                },
                Looper.getMainLooper()
            )

        }

        private const val TAG = "Util"
    }
}